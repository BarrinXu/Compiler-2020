package MIR.IRType;

abstract public class IRBaseType {
    public IRBaseType(){}


    public int dim(){
        return 0;
    }
    public abstract int size();
    public boolean needPtrLoad(){
        return false;
    }
    public abstract boolean sameType(IRBaseType rhs);
}
