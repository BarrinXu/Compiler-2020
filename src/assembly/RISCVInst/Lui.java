package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.Reg;

public class Lui extends BaseInst{
    public Imm address;

    public Lui(Reg dest, LIRBlock LBlock,Imm address) {
        super(dest, LBlock);
        this.address=address;
    }

    @Override
    public String toString() {
        return "lui "+dest+", "+address;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
