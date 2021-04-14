package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.HashSet;

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

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        tmp.add(rs1);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(rs1==origin)
            rs1=to;
    }
}
