package Util;

import AST.functionNode;
import Util.error.internalError;

public class funcDecl extends BaseType{
    public String name;
    public Type type;
    public functionScope localScope;

    public funcDecl(String name){
        super("function_Declaration_"+name);
        this.name=name;
        this.typeCategory=TypeCategory.FUNC;
    }

    public void addParameter(varEntity parameter, position pos){
        localScope.addParameter(parameter,pos);
    }


    @Override
    public boolean sameType(Type type){
        return this.type.sameType(type);
    }
    @Override
    public int dim(){
        throw new internalError("",new position(0,0));
    }

}
