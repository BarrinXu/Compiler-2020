package Util;

public class constructorType extends BaseType{
    public constructorType(){
        super("constructor__");
        typeCategory=TypeCategory.CONSTRUCTOR;
    }



    @Override
    public boolean sameType(Type type) {
        return type.isConstructor();
    }
}
