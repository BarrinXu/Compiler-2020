package Util;

import Util.error.internalError;

public class constructorType extends BaseType{
    public constructorType(){
        super("constructor__");
        typeCategory=TypeCategory.CONSTRUCTOR;
    }



    @Override
    public boolean sameType(Type type) {
        return type.isConstructor();
    }

    @Override
    public int size() {
        throw new internalError("",new position(0,0));
    }
}
