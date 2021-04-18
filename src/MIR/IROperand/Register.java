package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.IRBaseType;

import java.util.HashSet;

public class Register extends IRBaseOperand{

    public String name;
    public Inst defInst;
    public HashSet<Inst> usedInsts=new HashSet<>();


    public Register(IRBaseType type,String name) {
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

    public void replaceUse(IRBaseOperand newOperand){
        usedInsts.forEach(inst->{
            inst.modifyReg(this,newOperand);
            newOperand.addUsedInst(inst);
        });
    }

    @Override
    public String getIdentity() {
        return "%"+name;
    }

    @Override
    public IRBaseOperand copy() {
        return new Register(type,name);
    }
}
