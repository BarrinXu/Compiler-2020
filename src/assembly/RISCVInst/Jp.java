package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {

    }
}
