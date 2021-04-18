package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;

public class Zext extends Inst{
    public IRBaseOperand origin;

    public Zext(Register reg, IRBlock block,IRBaseOperand origin) {
        super(reg, block);
        this.origin=origin;
        origin.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        origin.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(origin==oriReg)
            origin=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new Zext((Register) getMirOperand(reg,mirOperands),newBlock,getMirOperand(origin,mirOperands)));
    }
}
