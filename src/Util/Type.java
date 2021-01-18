package Util;

import java.util.HashMap;

abstract public class Type {
    public enum TypeCategory{
        NULL, INT, BOOL, ARRAY, CLASS, CONSTRUCTOR, VOID, FUNC
    }

    public abstract int dim();
    public TypeCategory typeCategory;

    public abstract boolean sameType(Type it);

    public boolean isNull(){
        return typeCategory==TypeCategory.NULL;
    }
    public boolean isInt(){
        return typeCategory==TypeCategory.INT;
    }
    public boolean isBool(){
        return typeCategory==TypeCategory.BOOL;
    }
    public boolean isArray(){
        return typeCategory==TypeCategory.ARRAY;
    }
    public boolean isClass(){
        return typeCategory==TypeCategory.CLASS;
    }
    public boolean isVoid(){
        return typeCategory==TypeCategory.VOID;
    }
    public boolean isConstructor(){
        return typeCategory==TypeCategory.CONSTRUCTOR;
    }

}
