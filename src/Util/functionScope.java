package Util;
import java.util.ArrayList;

public class functionScope extends Scope{
    public ArrayList<varEntity> parameters=new ArrayList<>();
    public functionScope(Scope faScope){
        super(faScope);
    }
    public void addParameter(varEntity parameter, position pos){
        parameters.add(parameter);
        defMember(parameter.name,parameter,pos);
    }
}
