package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;

public class Li extends BaseInst{
    public Imm val;

    public Li(Reg dest, LIRBlock LBlock,Imm val) {
        super(dest, LBlock);
        this.val=val;
    }

    @Override
    public String toString() {
        return "li "+dest+", "+val;
    }

    @Override
    public void addStackSize(int stackSize) {
        if(val instanceof StackImm)
            val=new Imm(val.val+stackSize);
    }
}
