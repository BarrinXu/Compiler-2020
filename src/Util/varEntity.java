package Util;

import MIR.IROperand.ConstInt;
import MIR.IROperand.IRBaseOperand;

public class varEntity extends Entity{
    public Type type;
    public boolean isGlobal, isMember;

    public IRBaseOperand operand;
    public ConstInt index;

    public varEntity(String name, Type type, boolean isGlobal){
        super(name);
        this.type=type;
        this.isGlobal=isGlobal;
        this.isMember=false;
    }
    public void setIndex(int index){
        this.index=new ConstInt(index,32);
    }

    public Type type(){
        return type;
    }

}
