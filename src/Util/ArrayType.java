package Util;



public class ArrayType extends Type {
    public BaseType baseType;
    public int dim;

    public ArrayType(BaseType baseType, int dim)
    {
        this.baseType=baseType;
        this.dim=dim;
        this.typeCategory=TypeCategory.ARRAY;
    }

    public ArrayType(Type array){
        this.baseType=((ArrayType)array).baseType;
        this.dim=array.dim()-1;
		this.typeCategory=TypeCategory.ARRAY;
    }

    @Override
    public int dim()
    {
        return dim;
    }


    @Override
    public boolean sameType(Type type) {
        return type.isNull()||(dim==type.dim()&&baseType.sameType(((ArrayType)type).baseType));
    }




}
