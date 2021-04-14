package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;
import assembly.LRoot;

import java.util.Collections;
import java.util.HashSet;

public class Ret extends BaseInst{
    public LRoot root;

    public Ret(LIRBlock LBlock,LRoot root) {
        super(null, LBlock);
        this.root=root;
    }

    @Override
    public String toString() {
        return "ret";
    }

    @Override
    public void addStackSize(int stackSize) {

    }

    @Override
    public HashSet<Reg> usedRegSet() {
        //maybe change!
        return new HashSet<>(Collections.singleton(root.realRegs.get(1)));
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {

    }
}
