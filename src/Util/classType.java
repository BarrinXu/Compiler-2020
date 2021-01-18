package Util;
import Util.position;
import java.util.ArrayList;
public class classType extends BaseType{
    public Scope localScope;

    public classType(String name){
        super(name);
        typeCategory=TypeCategory.CLASS;
    }


    public void defMethod(String name, funcDecl func, position pos){
        localScope.defMethod(name,func,pos);
    }


    @Override
    public boolean sameType(Type it){
        return it.isNull()||(it.dim()==0&&it.isClass()&&this.name().equals(((classType)it).name()));
    }
}
