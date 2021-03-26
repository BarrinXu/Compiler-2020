package BackEnd;

import MIR.IRInst.Move;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class parallelCopy {
    public ArrayList<Move> copys=new ArrayList<>();
    public HashMap<IRBaseOperand,Integer> regMap =new HashMap<>();

    public void addMove(Move inst){
        copys.add(inst);
        var originOperand=inst.oriOperand;
        if(originOperand instanceof Register){
            if(regMap.containsKey(originOperand))
                regMap.put(originOperand, regMap.get(originOperand)+1);
            else
                regMap.put(originOperand,1);
        }
    }
}
