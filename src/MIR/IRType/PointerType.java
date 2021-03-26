package MIR.IRType;

public class PointerType extends IRBaseType{
    public int dim;
    public IRBaseType dest;
    public boolean needPtrLoad;
    public PointerType(IRBaseType dest,boolean needPtrLoad){
        super();
        this.dest=dest;
        this.dim=dest.dim()+1;
        this.needPtrLoad=needPtrLoad;
    }
    @Override
    public int dim(){
        return dim;
    }
    @Override
    public int size() {
        return 32;
    }
    @Override
    public boolean needPtrLoad(){
        return needPtrLoad;
    }
}
