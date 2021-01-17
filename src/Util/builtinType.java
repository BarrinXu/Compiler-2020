package Util;
import Util.error.internalError;



public class builtinType extends BaseType{
    private TypeCategory typeCategory;
    public builtinType(String name, TypeCategory typeCategory){
        super(name);
        this.typeCategory=typeCategory;
    }

    @Override
    public TypeCategory typeCategory(){
        return typeCategory;
    }
    @Override
    public boolean sameType(Type type){
        return (isNull()&&(type.isArray()||type.isClass()))||(typeCategory==type.typeCategory()&&type.dim()==0);
    }
}
