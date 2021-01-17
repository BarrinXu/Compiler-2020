package Util;
import Util.position;
import java.util.ArrayList;
public class classType extends BaseType{
    private Scope localScope;
    private varEntity formalEntity;
    public ArrayList<Type> elementTypeList=new ArrayList<>();

    public classType(String name){
        super(name);
        formalEntity=new varEntity("formal entity",this,false);
    }
    public varEntity formalEntity(){
        return formalEntity;
    }

    public Scope scope(){
        return localScope;
    }

    public void addScope(Scope localScope){
        this.localScope=localScope;
    }
    public void defineMethod(String name, funcDecl func, position pos){
        localScope.defineMethod(name,func,pos);
    }
    public void setElement(Type type){
        elementTypeList.add(type);
    }

    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.CLASS;
    }

    @Override
    public boolean sameType(Type it){
        return it.isNull()||(it.dim()==0&&it.isClass()&&this.name().equals(((classType)it).name()));
    }
}
