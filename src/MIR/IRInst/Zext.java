package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Zext extends Inst{
    public IRBaseOperand origin;

    public Zext(Register reg, IRBlock block,IRBaseOperand origin) {
        super(reg, block);
        this.origin=origin;
        origin.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove() {
        block.removeInst(this);
        origin.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(origin==oriReg)
            origin=to;
    }
}
