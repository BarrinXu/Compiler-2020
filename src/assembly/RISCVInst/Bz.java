package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.HashSet;

public class Bz extends BaseInst{
    public Reg branchResult;
    public CmpOpType opCode;
    public LIRBlock destBlock;

    public Bz(LIRBlock LBlock,Reg branchResult,CmpOpType opCode,LIRBlock destBlock) {
        super(null, LBlock);
        this.branchResult=branchResult;
        this.opCode=opCode;
        this.destBlock=destBlock;
    }

    @Override
    public String toString() {
        return opCode+"z "+branchResult+", "+destBlock;
    }

    @Override
    public void addStackSize(int stackSize) {

    }

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg> tmp=new HashSet<>();
        tmp.add(branchResult);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(branchResult==origin)
            branchResult=to;
    }
}
