package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Move extends Inst{
    public IRBaseOperand oriOperand;

    public Move(Register reg, IRBlock block,IRBaseOperand oriOperand,boolean addUsedInst) {
        super(reg, block);
        this.oriOperand=oriOperand;
        if(addUsedInst)
            oriOperand.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        oriOperand.removeUsedInst(this);
    }

    public void modifyReg(Register oriReg, IRBaseOperand to){
        if(oriOperand==oriReg)
            oriOperand=to;
    }
}
