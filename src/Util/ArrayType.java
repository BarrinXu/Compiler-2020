package Util;



public class ArrayType extends Type {
    public BaseType baseType;
    public int dim;

    public ArrayType(BaseType baseType, int dim)
    {
        this.baseType=baseType;
        this.dim=dim;
    }

    public ArrayType(Type array){
        this.baseType=((ArrayType)array).baseType();
        this.dim=array.dim()-1;
    }

    @Override
    public int dim()
    {
        return dim;
    }

    @Override
    public TypeCategory typeCategory() {
        return TypeCategory.ARRAY;
    }

    @Override
    public boolean sameType(Type type) {
        return type.isNull()||(dim==type.dim()&&baseType.sameType(type.baseType()));
    }

    @Override
    public BaseType baseType() {
        return baseType;
    }


}
