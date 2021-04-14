package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {

    }
}
