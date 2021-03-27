package assembly.LOperand;

import assembly.RISCVInst.BaseInst;
import assembly.RISCVInst.IType;
import assembly.RISCVInst.Mv;

import static java.lang.Math.abs;

public class Imm extends LOperand{
    public int val;
    public int limit=1023;
    public Imm(int val){
        this.val=val;
    }

    @Override
    public String toString() {
        return val+"";
    }
    public StringBuilder workOverLimit(Reg var, Reg init){
        StringBuilder addInst;
        addInst = new StringBuilder(new Mv(var, null, init).toString() + "\n");
        while(abs(val)>limit)
        {
            int id=val>0?1:-1;
            addInst.append("\t");
            addInst.append(new IType(var, null, var, new Imm(id * limit), BaseInst.CalOpType.add).toString());
            addInst.append("\n");
            val-=id*limit;
        }
        return addInst;
    }
}
