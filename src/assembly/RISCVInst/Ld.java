package assembly.RISCVInst;

import MIR.IROperand.GlobalRegister;
import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.LGlobalReg;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;

public class Ld extends BaseInst{
    public Reg address;
    public Imm offset;
    public int size;

    public Ld(Reg dest, LIRBlock LBlock,Reg address,Imm offset,int size) {
        super(dest, LBlock);
        this.address=address;
        this.offset=offset;
        this.size=size;
    }

    @Override
    public String toString() {
        String dataType;
        if(size==1)
            dataType="b";
        else if(size==4)
            dataType="w";
        else
            dataType="h";
        return "l"+dataType+" "+dest+", "+((address instanceof LGlobalReg)?address:offset+"("+address+")");
    }

    @Override
    public void addStackSize(int stackSize) {
        if(offset instanceof StackImm)
            offset=new Imm(stackSize+offset.val);
    }
}
