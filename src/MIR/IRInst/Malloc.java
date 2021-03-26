package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Malloc extends Inst{
    public IRBaseOperand size;

    public Malloc(Register reg, IRBlock block, IRBaseOperand size) {
        super(reg, block);
        this.size=size;
        size.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove() {
        block.removeInst(this);
        size.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(size==oriReg)
            size=to;
    }
}
