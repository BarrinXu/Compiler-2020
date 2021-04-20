package MIR.IRType;

public class BoolType extends IRBaseType{
    public BoolType(){
        super();
    }

    @Override
    public int size() {
        return 8;
    }

    @Override
    public boolean sameType(IRBaseType rhs) {
        return rhs instanceof BoolType;
    }
}
