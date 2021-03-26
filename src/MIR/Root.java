package MIR;

import MIR.IROperand.ConstString;
import MIR.IROperand.GlobalRegister;
import MIR.IROperand.Parameter;
import MIR.IRType.*;
import Util.ArrayType;
import Util.Type;
import Util.classType;

import java.util.ArrayList;
import java.util.HashMap;

public class Root {

    public static IRBaseType string_type=new PointerType(new IntType(8),false), void_type=new VoidType(), int32_type=new IntType(32), char_type=new IntType(8), bool_type=new BoolType();

    public HashMap<String,Function> functions=new HashMap<>();
    public HashMap<String,Function> builtInFunctions=new HashMap<>();
    public HashMap<String,ClassType> ClassTypes=new HashMap<>();


    public ArrayList<GlobalRegister>globalRegisters=new ArrayList<>();
    public HashMap<String, ConstString> constStrings=new HashMap<>(),stringVals=new HashMap<>();
    public void addConstString(String name,String val){
        if(!constStrings.containsKey(name)){
            ConstString operand=new ConstString(name,val);
            constStrings.put(name,operand);
            stringVals.put(val,operand);
        }
    }

    public Root(){
        Function func =new Function("BI_print");
        func.returnType=void_type;
        func.parameters.add(new Parameter(string_type,"str"));
        builtInFunctions.put("BI_print",func);

        func=new Function("BI_println");
        func.returnType=void_type;
        func.parameters.add(new Parameter(string_type,"str"));
        builtInFunctions.put("BI_println",func);

        func=new Function("BI_printInt");
        func.returnType=void_type;
        func.parameters.add(new Parameter(int32_type,"n"));
        builtInFunctions.put("BI_printInt",func);

        func=new Function("BI_printlnInt");
        func.returnType=void_type;
        func.parameters.add(new Parameter(int32_type,"n"));
        builtInFunctions.put("BI_printlnInt",func);

        func=new Function("BI_getString");
        func.returnType=string_type;
        builtInFunctions.put("BI_getString",func);

        func=new Function("BI_getInt");
        func.returnType=int32_type;
        builtInFunctions.put("BI_getInt",func);

        func=new Function("BI_toString");
        func.returnType=string_type;
        func.parameters.add(new Parameter(int32_type,"i"));
        builtInFunctions.put("BI_toString",func);

        func=new Function("BI_str_add");
        func.returnType=string_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_add",func);

        func=new Function("BI_str_EQ");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_EQ",func);

        func=new Function("BI_str_NEQ");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_NEQ",func);

        func=new Function("BI_str_LT");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_LT",func);

        func=new Function("BI_str_GT");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_GT",func);

        func=new Function("BI_str_LE");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_LE",func);

        func=new Function("BI_str_GE");
        func.returnType=bool_type;
        func.parameters.add(new Parameter(string_type,"a"));
        func.parameters.add(new Parameter(string_type,"b"));
        builtInFunctions.put("BI_str_GE",func);

        func=new Function("INIT");
        func.outBlock=func.inBlock;
        func.returnType=new VoidType();
        functions.put("INIT",func);

        func=new Function("malloc");
        func.returnType=string_type;
        func.parameters.add(new Parameter(int32_type,"length"));
        builtInFunctions.put("malloc",func);
    }

    public Function getFunction(String name){
        if(functions.containsKey(name))
            return functions.get(name);
        else
            return builtInFunctions.get(name);
    }

    public IRBaseType getType(Type type){
        if(type instanceof ArrayType){
            IRBaseType ret=getType(((ArrayType) type).baseType);
            for(int i=0; i<((ArrayType) type).dim; i++)
                ret=new PointerType(ret,false);
            return ret;
        }
        else if(type.isInt())
            return Root.int32_type;
        else if(type.isBool())
            return new BoolType();
        else if(type.isVoid())
            return new VoidType();
        else if(type.isClass()){
            if(((classType)type).name.equals("string"))
                return Root.string_type;
            else
                return new PointerType(ClassTypes.get(((classType)type).name),false);
        }
        else
            return new VoidType();
    }
}
