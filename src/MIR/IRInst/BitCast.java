package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class BitCast extends Inst{
    public IRBaseOperand it;

    public BitCast(Register reg, IRBlock block,IRBaseOperand it) {
        super(reg, block);
        this.it=it;
        it.addUsedInst(this);
        reg.defInst=this;
    }


    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        it.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(it==oriReg)
            it=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new BitCast((Register) getMirOperand(reg,mirOperands),newBlock,getMirOperand(it,mirOperands)));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(it);
        return tmp;
    }
}
