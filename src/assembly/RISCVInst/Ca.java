package assembly.RISCVInst;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.Reg;
import assembly.LRoot;

public class Ca extends BaseInst{
    public LFunction func;
    public LRoot root;

    public Ca(LIRBlock LBlock,LFunction func,LRoot root) {
        super(null, LBlock);
        this.func=func;
        this.root=root;
    }
    @Override
    public String toString(){
        return "call "+func.name;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
