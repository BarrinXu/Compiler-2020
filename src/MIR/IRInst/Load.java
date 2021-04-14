package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Load extends Inst{
    public IRBaseOperand address;
    public Load(Register reg,IRBaseOperand address,IRBlock block) {
        super(reg, block);
        this.address=address;
        address.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        address.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(address==oriReg)
            address=to;
    }
}
