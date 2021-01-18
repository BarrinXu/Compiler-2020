package Util;

import Util.error.semanticError;

import java.util.HashMap;

public class Scope {

    public HashMap<String, varEntity> members=new HashMap<>();
    public HashMap<String, funcDecl> methods=new HashMap<>();
    public funcDecl constructor=null;
    public Scope faScope;

    public Scope(Scope faScope) {
        this.faScope = faScope;
    }


    public void defMember(String  name, varEntity var, position pos){
        if(members.containsKey(name))
            throw new semanticError("",pos);
        members.put(name,var);
    }

    public void defMethod(String name, funcDecl func, position pos){
        if(methods.containsKey(name))
            throw new semanticError("",pos);
        methods.put(name,func);
    }

    public void defConstructor(funcDecl func, position pos){
        if(constructor!=null)
            throw new semanticError("",pos);
        constructor=func;
    }

    public boolean haveMember(String name, boolean searchUpon){
        if(members.containsKey(name))
            return true;
        else if(faScope!=null&&searchUpon)
            return faScope.haveMember(name,true);
        else
            return false;
    }

    public boolean haveMethod(String name, boolean searchUpon){
        if(methods.containsKey(name))
            return true;
        else if(faScope!=null&&searchUpon)
            return faScope.haveMethod(name,true);
        else
            return false;
    }


    public Type getMemberType(String name, position pos, boolean searchUpon){
        if(members.containsKey(name))
            return members.get(name).type();
        else if(faScope!=null&&searchUpon)
            return faScope.getMemberType(name,pos,true);
        else
            throw new semanticError("",pos);
    }


    public funcDecl getMethodType(String name, position pos, boolean searchUpon){
        if(methods.containsKey(name))
            return methods.get(name);
        else if(faScope!=null&&searchUpon)
            return faScope.getMethodType(name,pos,true);
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
