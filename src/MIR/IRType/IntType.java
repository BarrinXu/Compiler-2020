package MIR.IRType;

public class IntType extends IRBaseType{
    public int size;
    public IntType(int size){
        super();
        this.size=size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean sameType(IRBaseType rhs) {
        return rhs instanceof IntType&&rhs.size()==size();
    }
}
