package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;

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
        return "s"+dataType+" "+val+", "+offset+"("+address+")";
    }

    @Override
    public void addStackSize(int stackSize) {
        if(offset instanceof StackImm)
            offset=new Imm(stackSize+offset.val);
    }
}
