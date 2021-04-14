package Optimize;

import BackEnd.DomAnalysis;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Jump;
import MIR.IROperand.IRBaseOperand;
import MIR.Root;

import java.util.HashMap;
import java.util.HashSet;

public class ConstantPropagation {
    public Root IRRoot;
    public boolean success,changed;
    public Function nowFunc;

    public HashMap<IRBaseOperand,IRBaseOperand>constMap=new HashMap<>();
    public HashSet<IRBlock> visitBlocks=new HashSet<>();
    public HashSet<IRBlock> reachBlocks=new HashSet<>();


    public ConstantPropagation(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public boolean solve(){
        success=false;
        constMap=new HashMap<>();
        IRRoot.functions.forEach((name,func)->solveFunc(func));
        return success;
    }

    public void blockDFS(IRBlock block){
        block.sons.forEach(son->{
            if(!reachBlocks.contains(son)){
                reachBlocks.add(son);
                blockDFS(son);
            }
        });
    }

    public void reachAnalysis(Function func){
        reachBlocks=new HashSet<>();
        reachBlocks.add(func.inBlock);
        blockDFS(func.inBlock);
    }

    public void solveFunc(Function func){
        nowFunc=func;
        changed=true;
        boolean needUpd=false;
        while(changed){
            changed=false;
            visitBlocks.clear();
            solveBlock(func.inBlock);
            reachAnalysis(func);
            func.blocks.forEach(block -> {
                if(!reachBlocks.contains(block)){
                    block.deleteTerminate();
                    block.setTerminate(new Jump(block,block));//why??
                }
            });
            success|=changed;
            needUpd|=changed;
        }
        if(needUpd)
            new DomAnalysis(func).solve();
    }
    public void solveBlock(IRBlock block){

    }
}
