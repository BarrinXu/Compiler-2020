package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        tmp.add(ori);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(ori==origin)
            ori=to;
    }
}
