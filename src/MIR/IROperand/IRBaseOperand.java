package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;

abstract public class IRBaseOperand {
    public IRBaseType type;
    public IRBaseOperand(IRBaseType type){
        this.type=type;
    }

    public abstract void addUsedInst(Inst inst);
    public abstract void removeUsedInst(Inst inst);
    public abstract String getIdentity();

}
