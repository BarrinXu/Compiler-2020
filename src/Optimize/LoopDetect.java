package Optimize;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Jump;
import MIR.IRInst.Phi;
import MIR.IROperand.Register;

import java.util.*;

public class LoopDetect {
    public class Loop{
        HashSet<IRBlock> blocks=new HashSet<>();
        HashSet<IRBlock> tails=new HashSet<>();
        HashSet<Loop> sons=new HashSet<>();
        public IRBlock preHead;
    }
    public Function func;
    public HashMap<IRBlock,Loop>loopMap=new HashMap<>();
    public HashSet<Loop> rootLoops=new HashSet<>();
    public HashSet<IRBlock> visitBlocks=new HashSet<>();
    public Stack<Loop> loopStack=new Stack<>();
    public LoopDetect(Function func){
        this.func=func;
    }
    public void solve(){
        func.blocks.forEach(block -> {
            for(var son:block.sons)
                if(block.searchDom(son)){
                    if(!loopMap.containsKey(son))
                        loopMap.put(son,new Loop());
                    loopMap.get(son).tails.add(block);
                    break;
                }
        });
        loopMap.forEach(this::addPreHead);
        loopMap.forEach((headBlock,loop)->{
            loop.tails.forEach(tailBlock ->{
                loopBlockCollect(headBlock, tailBlock);
            });
        });
        DFS(func.inBlock);
    }
    public void addPreHead(IRBlock head,Loop loop){
        ArrayList<IRBlock> fas=new ArrayList<>(head.fas);
        fas.removeAll(loop.tails);
        if(fas.size()==1)
            loop.preHead= fas.get(0);
        else {
            var preHead=new IRBlock(head.name+"_AddLoopPreHead");
            func.blocks.add(preHead);
            for(var iter=head.phiInstMap.entrySet().iterator(); iter.hasNext(); ){
                var phiInst=iter.next().getValue();
                Phi mirPhi=null;
                boolean canRemove=true;
                for(int i=0; i<phiInst.operands.size(); i++){
                    if(!loop.tails.contains(phiInst.blocks.get(i))){
                        if(mirPhi==null){
                            mirPhi=new Phi(new Register(phiInst.reg.type,phiInst.reg.name+"_PreHeadPhi"),preHead,new ArrayList<>(),new ArrayList<>());
                            //preHead.add_PhiInst(mirPhi);
                        }
                        mirPhi.addOrigin(phiInst.operands.get(i),phiInst.blocks.get(i));
                        phiInst.operands.remove(i);
                        phiInst.blocks.remove(i);
                        i--;
                    }
                    else
                        canRemove=false;
                }
                if(canRemove){
                    iter.remove();
                    phiInst.reg.replaceUse(mirPhi.reg);
                }
                else if(mirPhi!=null)
                    phiInst.addOrigin(mirPhi.reg, preHead);
            }
            fas.forEach(fa->{
                fa.modifySon(head,preHead);
            });
            preHead.setTerminate(new Jump(preHead,head));
            loop.preHead=preHead;
        }
    }
    public void loopBlockCollect(IRBlock head,IRBlock tail){
        HashSet<IRBlock> visit=new HashSet<>();
        Queue<IRBlock>queue=new LinkedList<>();
        queue.add(tail);
        visit.add(head);
        visit.add(tail);
        while(!queue.isEmpty()){
            var nowBlock=queue.poll();
            nowBlock.fas.forEach(fa->{
                if(!visit.contains(fa)){
                    queue.add(fa);
                    visit.add(fa);
                }
            });
        }
        loopMap.get(head).blocks.addAll(visit);
    }
    public void DFS(IRBlock block){
        visitBlocks.add(block);
        while(!loopStack.isEmpty()&&!loopStack.peek().blocks.contains(block))
            loopStack.pop();
        if(loopMap.containsKey(block)){
            var loop=loopMap.get(block);
            if(loopStack.isEmpty())
                rootLoops.add(loop);
            else
                loopStack.peek().sons.add(loop);
            loopStack.push(loop);
        }
        block.loopDep=loopStack.size();
        block.sons.forEach(son->{
            if(!visitBlocks.contains(son))
                DFS(son);
        });
    }
}
