package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;

import java.util.HashSet;

public class GlobalRegister extends IRBaseOperand{
    public String name;
    public HashSet<Inst> UsedInsts=new HashSet<>();
    public GlobalRegister(IRBaseType type, String name) {
        super(type);
        this.name=name;
    }

    @Override
    public void addUsedInst(Inst inst) {
        UsedInsts.add(inst);
    }

    @Override
    public void removeUsedInst(Inst inst) {
        UsedInsts.remove(inst);
    }

    @Override
    public String getIdentity() {
        return "@"+name;
    }
}
