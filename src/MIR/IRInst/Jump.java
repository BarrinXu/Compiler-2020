package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Jump extends Inst{
    public IRBlock destBlock;
    public Jump(IRBlock block, IRBlock destBlock) {
        super(null, block);
        this.destBlock=destBlock;
    }

    @Override
    public void remove() {
        block.deleteTerminate();
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {

    }
}
