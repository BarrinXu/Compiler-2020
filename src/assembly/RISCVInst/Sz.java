package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

public class Sz extends BaseInst{
    public Reg rs1;
    public CmpOpType opCode;

    public Sz(Reg dest, LIRBlock LBlock,Reg rs1,CmpOpType opCode) {
        super(dest, LBlock);
        this.rs1=rs1;
        this.opCode=opCode;
    }

    @Override
    public String toString() {
        String op;
        if(opCode==CmpOpType.beq)
            op="seqz";
        else if(opCode==CmpOpType.bne)
            op="snez";
        else throw new InternalError("opCodeTransferFail");
        return op+" "+dest+", "+rs1;
    }

    @Override
    public void addStackSize(int stackSize) {

    }
}
