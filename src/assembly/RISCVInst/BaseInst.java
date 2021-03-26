package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

public abstract class BaseInst {
    public Reg dest;
    public LIRBlock LBlock;

    public BaseInst lst=null,nxt=null;

    public enum CalOpType{
        add,sub,sll,slt,sltu,xor,srl,sra,or,and,mul,div,rem
    }
    public enum CmpOpType{
        beq,bne
    }

    public BaseInst(Reg dest,LIRBlock LBlock){
        this.dest=dest;
        this.LBlock=LBlock;
    }
    public abstract String toString();
    public abstract void addStackSize(int stackSize);
}
