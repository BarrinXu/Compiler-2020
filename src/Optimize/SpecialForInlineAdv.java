package Optimize;

import MIR.IRBlock;
import MIR.IRInst.Binary;
import MIR.IROperand.ConstInt;
import MIR.Root;

public class SpecialForInlineAdv {
    public Root IRRoot;
    public boolean success;
    public SpecialForInlineAdv(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public boolean solve(){
        success=false;
        IRRoot.functions.forEach((name,func)->{
            func.blocks.forEach(this::solveBlock);
        });
        return success;
    }
    public void solveBlock(IRBlock block){
        for(var inst=block.head; inst!=null&&inst.nxt!=null; inst=inst.nxt){
            var nxtInst=inst.nxt;
            if(inst instanceof Binary&&nxtInst instanceof Binary){
                if(inst.reg==((Binary) nxtInst).lhs&&((Binary) inst).rhs instanceof ConstInt&&((Binary) nxtInst).rhs instanceof ConstInt){
                    //special for inline-adv
                    if(((Binary) inst).opCode== Binary.BinaryOpType.add&&((Binary) nxtInst).opCode== Binary.BinaryOpType.add){
                        ((Binary) inst).lhs.removeUsedInst(inst);
                        ((Binary) nxtInst).lhs.removeUsedInst(nxtInst);
                        ((Binary) nxtInst).lhs=((Binary) inst).lhs;
                        ((Binary) nxtInst).lhs.addUsedInst(nxtInst);
                        ((Binary) nxtInst).rhs=new ConstInt(((ConstInt) ((Binary) inst).rhs).val+((ConstInt) ((Binary) nxtInst).rhs).val,32);
                        inst.remove(true);
                        success=true;
                    }
                }
            }
        }
    }
}
