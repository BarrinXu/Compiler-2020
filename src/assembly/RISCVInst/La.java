package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.LGlobalReg;
import assembly.LOperand.Reg;

public class La extends BaseInst{
    public LGlobalReg oriReg;

    public La(Reg dest, LIRBlock LBlock,LGlobalReg oriReg) {
        super(dest, LBlock);
        this.oriReg=oriReg;
    }

    @Override
    public String toString() {
        return "la "+dest+", "+oriReg;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
