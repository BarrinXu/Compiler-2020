package Optimize;

import MIR.Function;
import MIR.IRInst.*;
import MIR.IROperand.GlobalRegister;
import MIR.IROperand.Register;
import MIR.IRType.BoolType;
import MIR.IRType.IntType;
import MIR.IRType.PointerType;
import MIR.IRType.VoidType;
import MIR.Root;
import com.sun.jdi.InternalException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class LoopInvariantCodeMotion {
    public Root IRRoot;
    public boolean success;
    public boolean hasCall;
    public LoopInvariantCodeMotion(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public HashSet<GlobalRegister>defGlobalsAddress=new HashSet<>();
    public boolean solve(){
        success=false;
        IRRoot.functions.forEach((name,func)->{
            solveFunc(func);
        });
        return success;
    }
    public void solveFunc(Function func){
        var loopDetect=new LoopDetect(func);
        loopDetect.solve();
        loopDetect.rootLoops.forEach(this::solveLoop);
    }
    public void solveLoop(LoopDetect.Loop loop){
        loop.sons.forEach(this::solveLoop);
        HashSet<Register> loopDefRegs=new HashSet<>();
        Queue<Inst>queue=new LinkedList<>();
        loop.blocks.forEach(block -> {
            block.phiInstMap.forEach((register, phiInst) -> {
                loopDefRegs.add(register);
            });
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                if(inst.reg!=null)
                    loopDefRegs.add(inst.reg);
        });
        hasCall=false;
        defGlobalsAddress.clear();
        loop.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                if(inst instanceof Store&&((Store) inst).address instanceof GlobalRegister)
                    defGlobalsAddress.add((GlobalRegister) ((Store) inst).address);
                else if(inst instanceof Call)
                    hasCall=true;
        });
        var preHead=loop.preHead;
        loop.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                tryModify(inst,loopDefRegs,queue);
        });
        success|=!queue.isEmpty();
        while(!queue.isEmpty()){
            var inst=queue.poll();
            inst.deleteInList();
            preHead.addInstBack(inst);
            inst.reg.usedInsts.forEach(usedInst->{
                if(loopDefRegs.contains(usedInst.reg))
                    tryModify(usedInst,loopDefRegs,queue);
            });
        }
    }
    public boolean judgeGlobalReg(GlobalRegister register){
        if(!(register.type instanceof PointerType))
            throw new InternalException();
        var to=((PointerType)register.type).dest;
        return to instanceof IntType||to instanceof BoolType||to instanceof VoidType;
    }
    public boolean judgeInstType(Inst inst){
        return !(inst instanceof Branch||inst instanceof Call||inst instanceof Jump||inst instanceof Load||inst instanceof Malloc||inst instanceof Phi||inst instanceof Return||inst instanceof Store);
    }
    public void tryModify(Inst inst, HashSet<Register>loopDefRegs, Queue<Inst>queue){
        if(inst instanceof Load){
            var usedInsts=inst.usedOperandSet();
            usedInsts.retainAll(loopDefRegs);
            if(usedInsts.isEmpty()&&((Load) inst).address instanceof GlobalRegister&&!hasCall&&judgeGlobalReg((GlobalRegister) ((Load) inst).address)&&!defGlobalsAddress.contains((GlobalRegister) ((Load) inst).address)){
                loopDefRegs.remove(inst.reg);
                queue.add(inst);
            }
        }
        else if(judgeInstType(inst)){
            var usedInsts=inst.usedOperandSet();
            usedInsts.retainAll(loopDefRegs);
            if(usedInsts.isEmpty()){
                loopDefRegs.remove(inst.reg);
                if(inst.reg==null){
                    System.out.println("!");
                }
                queue.add(inst);
            }
        }
    }
}
