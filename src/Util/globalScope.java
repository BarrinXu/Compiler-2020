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
        stringType.localScope=new Scope(this);
        types.put("string",stringType);
        position pos=new position(0,0);
        funcDecl tmpFunc;

        tmpFunc=new funcDecl("print");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("str",stringType),pos);
        tmpFunc.type=voidInstance;
        defMethod("print",tmpFunc,pos);

        tmpFunc=new funcDecl("println");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("str",stringType),pos);
        tmpFunc.type=voidInstance;
        defMethod("println",tmpFunc,pos);

        tmpFunc=new funcDecl("printInt");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance),pos);
        tmpFunc.type=voidInstance;
        defMethod("printInt",tmpFunc,pos);

        tmpFunc=new funcDecl("printlnInt");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance),pos);
        tmpFunc.type=voidInstance;
        defMethod("printlnInt",tmpFunc,pos);

        tmpFunc=new funcDecl("getString");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.type=stringType;
        defMethod("getString",tmpFunc,pos);

        tmpFunc=new funcDecl("getInt");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.type=intInstance;
        defMethod("getInt",tmpFunc,pos);

        tmpFunc=new funcDecl("toString");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("n",intInstance),pos);
        tmpFunc.type=stringType;
        defMethod("toString",tmpFunc,pos);

        tmpFunc=new funcDecl("size");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.type=intInstance;
        defMethod("size",tmpFunc,pos);

        tmpFunc=new funcDecl("length");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.type=intInstance;
        stringType.defMethod("length",tmpFunc,pos);

        tmpFunc=new funcDecl("substring");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("left",intInstance),pos);
        tmpFunc.addParameter(new varEntity("right",intInstance),pos);
        tmpFunc.type=stringType;
        stringType.defMethod("substring",tmpFunc,pos);

        tmpFunc=new funcDecl("parseInt");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.type=intInstance;
        stringType.defMethod("parseInt",tmpFunc,pos);

        tmpFunc=new funcDecl("ord");
        tmpFunc.localScope=(new functionScope(this));
        tmpFunc.addParameter(new varEntity("pos",intInstance),pos);
        tmpFunc.type=intInstance;
        stringType.defMethod("ord",tmpFunc,pos);
    }

    public void defineClass(String name, Type type, position pos){
        if(types.containsKey(name))
            throw new semanticError("",pos);
        if(haveMember(name,false))
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
