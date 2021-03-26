package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

public class Mv extends BaseInst{
    public Reg ori;

    public Mv(Reg dest, LIRBlock LBlock,Reg ori) {
        super(dest, LBlock);
        this.ori=ori;
    }

    @Override
    public String toString() {
        return "mv "+dest+", "+ori;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
