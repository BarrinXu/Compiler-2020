package MIR.IRType;

public class ArrayType extends IRBaseType{
    public int size;
    public IRBaseType type;
    public ArrayType(int size,IRBaseType type){
        super();
        this.size=size;
        this.type=type;
    }

    @Override
    public int size() {
        return type.size();
    }

    @Override
    public boolean sameType(IRBaseType rhs) {
        return (rhs instanceof PointerType&&(((PointerType) rhs).dest.sameType(type)||((PointerType) rhs).dest instanceof VoidType))||(rhs instanceof ArrayType&&((ArrayType) rhs).type.sameType(type));
    }
}
