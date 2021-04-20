package MIR.IRType;

public class VoidType extends IRBaseType{
    public VoidType() {
        super();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean sameType(IRBaseType rhs) {
        return rhs instanceof VoidType;
    }
}
