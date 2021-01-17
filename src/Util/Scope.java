package Util;

import Util.error.semanticError;

import java.util.HashMap;

public class Scope {

    private HashMap<String, varEntity> members=new HashMap<>();
    private HashMap<String, funcDecl> methods=new HashMap<>();
    private funcDecl constructor=null;
    private Scope parentScope;

    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
    }

    public Scope parentScope() {
        return parentScope;
    }
    public void defineMember(String  name, varEntity var, position pos){
        if(members.containsKey(name))
            throw new semanticError("",pos);
        members.put(name,var);
    }

    public void defineMethod(String name, funcDecl func, position pos){
        if(methods.containsKey(name))
            throw new semanticError("",pos);
        methods.put(name,func);
    }

    public void defineConstructor(funcDecl func, position pos){
        if(constructor!=null)
            throw new semanticError("",pos);
        constructor=func;
    }

    public boolean containMember(String name, boolean searchUpon){
        if(members.containsKey(name))
            return true;
        else if(parentScope!=null&&searchUpon)
            return parentScope.containMember(name,true);
        else
            return false;
    }

    public boolean containMethod(String name, boolean searchUpon){
        if(methods.containsKey(name))
            return true;
        else if(parentScope!=null&&searchUpon)
            return parentScope.containMethod(name,true);
        else
            return false;
    }

    public funcDecl constructor(){
        return constructor;
    }

    public Type getMemberType(String name, position pos, boolean searchUpon){
        if(members.containsKey(name))
            return members.get(name).type();
        else if(parentScope!=null&&searchUpon)
            return parentScope.getMemberType(name,pos,true);
        else
            throw new semanticError("",pos);
    }

    public varEntity getMember(String name, position pos, boolean searchUpon){
        if(members.containsKey(name))
            return members.get(name);
        else if(parentScope!=null&&searchUpon)
            return parentScope.getMember(name,pos,true);
        else
            throw new semanticError("",pos);
    }

    public funcDecl getMethodType(String name, position pos, boolean searchUpon){
        if(methods.containsKey(name))
            return methods.get(name);
        else if(parentScope!=null&&searchUpon)
            return parentScope.getMethodType(name,pos,true);
        else
            throw new semanticError("",pos);
    }
    /*public void defineVariable(String name, Type t, position pos) {
        if (members.containsKey(name))
            throw new semanticError("Semantic Error: variable redefine", pos);
        members.put(name, t);
    }

    public boolean containsVariable(String name, boolean lookUpon) {
        if (members.containsKey(name)) return true;
        else if (parentScope != null && lookUpon)
            return parentScope.containsVariable(name, true);
        else return false;
    }
    public Type getType(String name, boolean lookUpon) {
        if (members.containsKey(name)) return members.get(name);
        else if (parentScope != null && lookUpon)
            return parentScope.getType(name, true);
        return null;
    }*/
}
