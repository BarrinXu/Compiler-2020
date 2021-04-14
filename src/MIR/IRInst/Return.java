package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Return extends Inst{
    public IRBaseOperand val;
    public Return(IRBlock block, IRBaseOperand val) {
        super(null, block);
        this.val=val;
        if(val!=null)
            val.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.deleteTerminate();
        if(val!=null)
            val.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(val==oriReg)
            val=to;
    }
}
