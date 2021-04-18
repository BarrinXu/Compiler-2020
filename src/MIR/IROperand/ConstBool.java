package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.BoolType;
import MIR.IRType.IRBaseType;

public class ConstBool extends IRBaseOperand{
    public boolean val;

    public ConstBool(boolean val) {
        super(new BoolType());
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
        return val?"1":"0";
    }

    @Override
    public IRBaseOperand copy() {
        return new ConstBool(val);
    }
}
