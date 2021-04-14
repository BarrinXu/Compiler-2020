package assembly.RISCVInst;

import MIR.IROperand.GlobalRegister;
import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.LGlobalReg;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;

import java.util.HashSet;

import static java.lang.Math.abs;

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
        if((!(address instanceof LGlobalReg))&&abs(offset.val)>offset.limit){
            String tmp=offset.workOverLimit(dest,address).toString();
            return tmp+"\t"+"l"+dataType+" "+dest+", "+offset+"("+dest+")";
        }
        return "l"+dataType+" "+dest+", "+((address instanceof LGlobalReg)?address:offset+"("+address+")");
    }

    @Override
    public void addStackSize(int stackSize) {
        if(offset instanceof StackImm)
            offset=new Imm(stackSize+offset.val);
    }

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        if(!(address instanceof LGlobalReg))
            tmp.add(address);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(address==origin)
            address=to;
    }
}
