package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Store extends Inst{
    public IRBaseOperand val, address;

    public Store(IRBaseOperand address, IRBaseOperand val, IRBlock block) {
        super(null, block);
        this.val=val;
        this.address=address;
        this.val.addUsedInst(this);
        this.address.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        val.removeUsedInst(this);
        address.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(val==oriReg)
            val=to;
        if(address==oriReg)
            address=to;
    }
}
