package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;
import MIR.IRType.PointerType;
import MIR.IRType.VoidType;

public class Null extends IRBaseOperand{
    public Null() {
        super(new PointerType(new VoidType(),false));
    }

    @Override
    public void addUsedInst(Inst inst) {

    }

    @Override
    public void removeUsedInst(Inst inst) {

    }

    @Override
    public String getIdentity() {
        return "NULL";
    }
}
