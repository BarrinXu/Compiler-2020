package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.*;

import java.util.HashSet;

import static java.lang.Math.abs;

public class St extends BaseInst{
    public Reg address;
    public Imm offset;
    public Reg val;
    public int size;

    public St(LIRBlock LBlock,Reg address,Imm offset,Reg val,int size) {
        super(null, LBlock);
        this.address=address;
        this.offset=offset;
        this.val=val;
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
        if(abs(offset.val)>offset.limit){
            String tmp=offset.workOverLimit(new RealReg("t6"),address).toString();
            return tmp+"\t"+"s"+dataType+" "+val+", "+offset+"("+"t6"+")";
        }
        return "s"+dataType+" "+val+", "+offset+"("+address+")";
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
        tmp.add(val);
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {
        if(address==origin)
            address=to;
        if(val==origin)
            val=to;
    }
}
