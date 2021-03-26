package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

public class Jp extends BaseInst{
    public LIRBlock destBlock;

    public Jp(LIRBlock LBlock,LIRBlock destBlock) {
        super(null, LBlock);
        this.destBlock=destBlock;
    }

    @Override
    public String toString() {
        return "j "+destBlock;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
