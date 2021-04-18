package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;

import java.util.HashSet;

public class Parameter extends IRBaseOperand{

    public String name;
    public HashSet<Inst> usedInsts=new HashSet<>();
    public Parameter(IRBaseType type,String name) {
        super(type);
        this.name=name;
    }

    @Override
    public void addUsedInst(Inst inst) {
        usedInsts.add(inst);
    }

    @Override
    public void removeUsedInst(Inst inst) {
        usedInsts.remove(inst);
    }

    @Override
    public String getIdentity() {
        return "%"+name;
    }

    @Override
    public IRBaseOperand copy() {
        return new Parameter(type,name);
    }
}
