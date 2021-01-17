package Util;
import java.util.ArrayList;

public class functionScope extends Scope{
    private ArrayList<varEntity> parameters=new ArrayList<>();
    public functionScope(Scope parentScope){
        super(parentScope);
    }
    public void addParameter(varEntity parameter, position pos){
        parameters.add(parameter);
        defineMember(parameter.name(),parameter,pos);
    }
    public ArrayList<varEntity> parameters(){
        return parameters;
    }
}
