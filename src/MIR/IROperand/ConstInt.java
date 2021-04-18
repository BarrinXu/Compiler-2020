package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;
import MIR.IRType.IntType;

public class ConstInt extends IRBaseOperand{
    public int val;

    public ConstInt(int val,int size) {
        super(new IntType(size));
        this.val=val;
    }

    @Override
    public void addUsedInst(Inst inst) {

    }

    @Override
    public void removeUsedInst(Inst inst) {

    }

    @Override
    public String getIdentity() {
        return val+"";
    }

    @Override
    public IRBaseOperand copy() {
        return new ConstInt(val, type.size());
    }
}
