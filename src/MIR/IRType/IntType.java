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
}
