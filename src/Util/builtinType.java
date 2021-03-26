package Util;
import Util.error.internalError;



public class builtinType extends BaseType{

    public builtinType(String name, TypeCategory typeCategory){
        super(name);
        this.typeCategory=typeCategory;
    }


    @Override
    public boolean sameType(Type type){
        return (isNull()&&(type.isArray()||type.isClass()))||(typeCategory==type.typeCategory&&type.dim()==0);
    }

    @Override
    public int size() {
        if(isInt())
            return 32;
        else if(isBool())
            return 8;
        else
            throw new internalError("",new position(0,0));
    }
}
