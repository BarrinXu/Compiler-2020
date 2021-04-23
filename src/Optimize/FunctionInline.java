package Optimize;

import BackEnd.DomAnalysis;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Call;
import MIR.IRInst.Return;
import MIR.IROperand.IRBaseOperand;
import MIR.Root;

import java.util.*;

public class FunctionInline {
    public Root IRRoot;
    public boolean success=false, found=false;
    public boolean force;
    public int forceLimit=150;
    public int inlineCnt=0;
    public int inlineTimesLimit=100;
    public HashSet<Function>visit=new HashSet<>();
    public HashMap<Call,Function>canInline=new HashMap<>();
    public HashSet<Function>ignoreFunc=new HashSet<>();
    public HashMap<Function,Integer>instCnt=new HashMap<>();
    public HashMap<Function,HashSet<Function>>callPair=new HashMap<>();

    public FunctionInline(Root IRRoot,boolean force){
        this.IRRoot=IRRoot;
        this.force=force;
    }
    public void lineCntAnalysis(){
        IRRoot.functions.forEach((name,func)->{
            int cnt=0;
            for(var block:func.blocks)
                for(var inst=block.head; inst!=null; inst=inst.nxt)
                    cnt++;
            instCnt.put(func,cnt);
        });
    }
    public HashSet<Function> useFunc=new HashSet<>();
    public void useFuncAnalysis(){
        Queue<Function>queue=new LinkedList<>();
        useFunc.clear();
        queue.add(IRRoot.getFunction("main"));
        useFunc.add(IRRoot.getFunction("main"));
        while(!queue.isEmpty()){
            var func=queue.poll();
            for(var block:func.blocks){
                for(var inst=block.head; inst!=null; inst=inst.nxt)
                    if(inst instanceof Call){
                        var callee=((Call) inst).func;
                        if(useFunc.contains(callee)||ignoreFunc.contains(callee))
                            continue;
                        useFunc.add(callee);
                        queue.add(callee);
                    }
            }
        }
        for(var iter=IRRoot.functions.entrySet().iterator(); iter.hasNext();){
            var func=iter.next().getValue();
            if(!ignoreFunc.contains(func)&&!useFunc.contains(func))
                iter.remove();
        }
    }
    public void forceInlineCollect(){
        IRRoot.functions.forEach((name,func)->{
            for(var block:func.blocks)
                for(var inst=block.head; inst!=null; inst=inst.nxt)
                    if(inst instanceof Call){
                        var callee=((Call) inst).func;
                        if(!ignoreFunc.contains(callee)&&instCnt.get(callee)<forceLimit){
                            canInline.put((Call) inst,func);
                            instCnt.put(func,instCnt.get(func)+instCnt.get(callee));
                        }
                    }
        });
    }
    public void updDom(){
        IRRoot.functions.forEach((name,func)->{
            new DomAnalysis(func).solve();
        });
    }
    public boolean solve(){
        if(force){
            ignoreFunc.clear();
            instCnt.clear();
            canInline.clear();
            success=false;
            ignoreFunc.addAll(IRRoot.builtInFunctions.values());
            ignoreFunc.add(IRRoot.getFunction("main"));
            lineCntAnalysis();
            forceInlineCollect();
            canInline.forEach(this::inlineOperate);
            updDom();
            useFuncAnalysis();
            return !canInline.isEmpty();
        }
        else{
            success=false;
            visit.addAll(IRRoot.builtInFunctions.values());
            ignoreFunc.addAll(IRRoot.builtInFunctions.values());
            ignoreFunc.add(IRRoot.getFunction("main"));
            IRRoot.functions.forEach((name,func)->{
                callPair.put(func,new HashSet<>());
            });
            IRRoot.functions.forEach((name,func)->{
                if(!visit.contains(func))
                    DFS(func);
            });
            inlineProceed();
            updDom();
            return success;
        }

    }
    public Stack<Function>stack=new Stack<>();
    public void DFS(Function func){
        visit.add(func);
        stack.push(func);
        boolean hasRing=false;
        for(var stackFunc:stack){
            if(func.callFunc.contains(stackFunc))
                hasRing=true;
            if(hasRing)
                ignoreFunc.add(stackFunc);
        }
        func.callFunc.forEach(call->{
            callPair.get(call).add(func);
            if(!visit.contains(call))
                DFS(call);
        });
        stack.pop();
    }
    /*public void inlineAnalysis(Function func){
        func.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt){
                if(inst instanceof Call&&!ignoreFunc.contains(((Call) inst).func)){
                    changed=true;
                    canInline.put(func, (Call) inst);
                }
            }
        });
    }*/
    public void inlineProceed(){
        found=true;
        while(found){
            found=false;
            canInline.clear();
            IRRoot.functions.forEach((name,func)->{
                func.blocks.forEach(block -> {
                    for(var inst=block.head; inst!=null; inst=inst.nxt){
                        if(inst instanceof Call&&!ignoreFunc.contains(((Call) inst).func)){
                            found=true;
                            canInline.put((Call) inst,func);
                        }
                    }
                });
            });
            success|=found;
            canInline.forEach(this::inlineOperate);
        }
        for(var iter=IRRoot.functions.entrySet().iterator(); iter.hasNext();){
            var func=iter.next().getValue();
            if(!ignoreFunc.contains(func))
                iter.remove();
            else if(callPair.get(func).size()==1&&callPair.get(func).contains(func))
                iter.remove();
        }
    }
    public void inlineOperate(Call inst,Function func){
        /*inlineCnt++;
        if(inlineCnt>inlineTimesLimit)
            return;*/
        HashMap<IRBaseOperand,IRBaseOperand>mirOperands=new HashMap<>();
        HashMap<IRBlock,IRBlock>mirBlocks=new HashMap<>();
        var nowBlock=inst.block;
        var callee=inst.func;

        for(int i=0; i<inst.parameters.size(); i++)
            mirOperands.put(callee.parameters.get(i),inst.parameters.get(i));
        HashSet<IRBlock> oriBlocks=new HashSet<>(callee.blocks);
        for(var block:oriBlocks)
            mirBlocks.put(block,new IRBlock(block.name+"_ToInline"));
        func.blocks.addAll(mirBlocks.values());
        for(var block:oriBlocks){
            var newBlock=mirBlocks.get(block);
            if(newBlock==null){
                System.out.println("!");
            }
            for(var oriInst=block.head; oriInst!=null; oriInst=oriInst.nxt)
                oriInst.addMirInst(newBlock,mirOperands,mirBlocks);
            block.phiInstMap.forEach(((register, phiInst) -> {
                phiInst.addMirInst(newBlock,mirOperands,mirBlocks);
            }));
        }
        var latterBlock=new IRBlock(nowBlock.name+"_splitPt2");
        nowBlock.split(latterBlock,inst);
        var outBlock=mirBlocks.get(callee.outBlock);
        var retInst=(Return)outBlock.tail;
        if(retInst.val!=null)
            inst.reg.replaceUse(retInst.val);
        outBlock.deleteTerminate();
        outBlock.mergeBlock(latterBlock);
        var inBlock=mirBlocks.get(callee.inBlock);
        func.blocks.remove(inBlock);
        nowBlock.mergeBlock(inBlock);
        if(func.outBlock==nowBlock&&inBlock!=outBlock)
            func.outBlock=outBlock;//maybe simpler
    }
}
