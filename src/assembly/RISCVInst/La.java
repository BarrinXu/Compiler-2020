package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.LGlobalReg;
import assembly.LOperand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {

    }
}
