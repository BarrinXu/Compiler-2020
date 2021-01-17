package Util;

import AST.typeNode;
import Util.error.semanticError;

import java.util.HashMap;

public class globalScope extends Scope {
    public HashMap<String, Type> types;
    public builtinType intInstance=new builtinType("int",Type.TypeCategory.INT);
    public builtinType boolInstance=new builtinType("bool",Type.TypeCategory.BOOL);
    public builtinType voidInstance=new builtinType("void",Type.TypeCategory.VOID);
    public builtinType nullInstance=new builtinType("null",Type.TypeCategory.NULL);
    public globalScope() {
        super(null);
        types=new HashMap<>();
        types.put("int",intInstance);
        types.put("bool",boolInstance);
        types.put("void",voidInstance);
        types.put("null",nullInstance);

        classType stringType=new classType("string");
        stringType.addScope(new classScope(this));
        types.put("string",stringType);
        position pos=new position(0,0);
        funcDecl tmpFunc;

        tmpFunc=new funcDecl("print",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("str",stringType,false),pos);
        tmpFunc.setReturnType(voidInstance);
        defineMethod("print",tmpFunc,pos);

        tmpFunc=new funcDecl("println",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("str",stringType,false),pos);
        tmpFunc.setReturnType(voidInstance);
        defineMethod("println",tmpFunc,pos);

        tmpFunc=new funcDecl("printInt",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance,false),pos);
        tmpFunc.setReturnType(voidInstance);
        defineMethod("printInt",tmpFunc,pos);

        tmpFunc=new funcDecl("printlnInt",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance,false),pos);
        tmpFunc.setReturnType(voidInstance);
        defineMethod("printlnInt",tmpFunc,pos);

        tmpFunc=new funcDecl("getString",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setReturnType(stringType);
        defineMethod("getString",tmpFunc,pos);

        tmpFunc=new funcDecl("getInt",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setReturnType(intInstance);
        defineMethod("getInt",tmpFunc,pos);

        tmpFunc=new funcDecl("toString",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance,false),pos);
        tmpFunc.setReturnType(stringType);
        defineMethod("toString",tmpFunc,pos);

        tmpFunc=new funcDecl("size",null);
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setReturnType(intInstance);
        defineMethod("size",tmpFunc,pos);

        tmpFunc=new funcDecl("length",null);
        tmpFunc.isMethod=true;
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setReturnType(intInstance);
        stringType.defineMethod("length",tmpFunc,pos);

        tmpFunc=new funcDecl("substring",null);
        tmpFunc.isMethod=true;
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("left",intInstance,false),pos);
        tmpFunc.addParameter(new varEntity("right",intInstance,false),pos);
        tmpFunc.setReturnType(stringType);
        stringType.defineMethod("substring",tmpFunc,pos);

        tmpFunc=new funcDecl("parseInt",null);
        tmpFunc.isMethod=true;
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.setReturnType(intInstance);
        stringType.defineMethod("parseInt",tmpFunc,pos);

        tmpFunc=new funcDecl("ord",null);
        tmpFunc.isMethod=true;
        tmpFunc.setScope(new functionScope(this));
        tmpFunc.addParameter(new varEntity("pos",intInstance,false),pos);
        tmpFunc.setReturnType(intInstance);
        stringType.defineMethod("ord",tmpFunc,pos);
    }

    public void defineClass(String name, Type type, position pos){
        if(types.containsKey(name))
            throw new semanticError("",pos);
        if(containMember(name,false))
            throw new semanticError("",pos);
        types.put(name,type);
    }

    public void addType(String name, Type t, position pos) {
        if (types.containsKey(name))
            throw new semanticError("multiple definition of " + name, pos);
        types.put(name, t);
    }
    public Type getTypeFromName(String name, position pos) {
        if (types.containsKey(name))
            return types.get(name);
        throw new semanticError("no such type: " + name, pos);
    }

    public Type makeType(typeNode node){
        if(node.dim!=0)
            return new ArrayType((BaseType)getTypeFromName(node.name,node.pos),node.dim);
        else
            return getTypeFromName(node.name,node.pos);
    }


    public boolean hasType(String name){
        return types.containsKey(name);
    }
}
