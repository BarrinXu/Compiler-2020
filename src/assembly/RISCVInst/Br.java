package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.HashSet;

public class Br extends BaseInst{
    public enum BrOpcode{
        eq,ne,lt,ge
    }
    public Reg rs1,rs2;
    public BrOpcode opCode;
    public LIRBlock destBlock;
    public Br(LIRBlock LBlock, Reg rs1, Reg rs2, BrOpcode opCode, LIRBlock destBlock){
        super(null,LBlock);
        this.rs1=rs1;
        this.rs2=rs2;
        this.opCode=opCode;
        this.destBlock=destBlock;
    }
    @Override
    public String toString() {
        return "b"+opCode+" "+rs1+", "+rs2+", "+destBlock;
    }

    @Override
    public void addStackSize(int stackSize) {

    }

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        tmp.add(rs1);
        tmp.add(rs2);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(rs1==origin)
            rs1=to;
        if(rs2==origin)
            rs2=to;
    }
}
