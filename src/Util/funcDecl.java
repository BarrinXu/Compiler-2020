package Util;

import AST.functionNode;
import Util.error.internalError;

public class funcDecl extends BaseType{
    public String name;
    public Type type;
    public functionNode defNode;
    public functionScope localScope;
    public boolean isMethod;

    public funcDecl(String name, functionNode defNode){
        super("funcDecl"+name);

        this.name=name;
        this.defNode=defNode;
        this.isMethod=false;
    }
    public void setScope(functionScope localScope){
        this.localScope=localScope;
    }
    public functionScope scope(){
        return localScope;
    }
    public void addParameter(varEntity parameter, position pos){
        localScope.addParameter(parameter,pos);
    }

    public void setReturnType(Type type){
        this.type=type;
    }

    @Override
    public boolean sameType(Type type){
        throw new internalError("",defNode.pos);
    }
    @Override
    public int dim(){
        throw new internalError("",defNode.pos);
    }
    @Override
    public TypeCategory typeCategory(){
        return TypeCategory.FUNC;
    }
}
