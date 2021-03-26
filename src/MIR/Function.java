package MIR;

import MIR.IROperand.Parameter;
import MIR.IROperand.Register;
import MIR.IRType.IRBaseType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Function {
    public String name;
    public IRBaseType returnType;
    public Parameter classPointer;

    public ArrayList<Parameter>parameters=new ArrayList<>();
    public LinkedHashSet<IRBlock> blocks=new LinkedHashSet<>();

    public HashSet<Register> registers=new HashSet<>();
    public HashSet<Function> callFunc=new HashSet<>();

    public IRBlock inBlock=new IRBlock("in"), outBlock;
    public Function(String name){
        this.name=name;
        blocks.add(inBlock);
    }
    public void setClassPointer(Parameter classPointer){
        this.classPointer=classPointer;
        parameters.add(classPointer);
    }

}
