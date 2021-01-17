package Util;

public class constructorType extends BaseType{
    public constructorType(){
        super("constructor__");
    }

    @Override
    public TypeCategory typeCategory() {
        return TypeCategory.CONSTRUCTOR;
    }

    @Override
    public boolean sameType(Type type) {
        return type.isConstructor();
    }
}
