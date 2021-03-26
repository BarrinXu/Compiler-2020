package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

public class RType extends BaseInst{
    public Reg rs1,rs2;
    public CalOpType opCode;

    public RType(Reg dest, LIRBlock LBlock,Reg rs1,Reg rs2,CalOpType opCode) {
        super(dest, LBlock);
        this.rs1=rs1;
        this.rs2=rs2;
        this.opCode=opCode;
    }

    @Override
    public String toString() {
        return opCode+" "+dest+", "+rs1+", "+rs2;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
