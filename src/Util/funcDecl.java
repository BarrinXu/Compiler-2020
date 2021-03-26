package Util;


import MIR.Function;
import Util.error.internalError;

public class funcDecl extends BaseType{
    public String name;
    public Type type;
    public functionScope localScope;
    public Function func;
    public boolean isClassMethod;

    public funcDecl(String name){
        super("function_Declaration_"+name);
        this.name=name;
        this.typeCategory=TypeCategory.FUNC;
        this.isClassMethod=false;
    }

    public void addParameter(varEntity parameter, position pos){
        localScope.addParameter(parameter,pos);
    }


    @Override
    public boolean sameType(Type type){
        return this.type.sameType(type);
    }

    @Override
    public int size() {
        throw new internalError("",new position(0,0));
    }

    @Override
    public int dim(){
        throw new internalError("",new position(0,0));
    }

}
