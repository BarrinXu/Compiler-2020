package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;
import assembly.LRoot;

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
}
