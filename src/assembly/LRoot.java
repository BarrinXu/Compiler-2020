package assembly;

import assembly.LOperand.LGlobalReg;
import assembly.LOperand.RealReg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class LRoot {
    public static ArrayList<String> RegName=new ArrayList<>(Arrays.asList("zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"));
    public static ArrayList<Integer> RegPrivilege=new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0, 1, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1 ,1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1));

    public ArrayList<RealReg> realRegs=new ArrayList<>();
    public ArrayList<RealReg> callerRegs =new ArrayList<>();
    public ArrayList<RealReg> calleeRegs=new ArrayList<>();
    public ArrayList<RealReg> assignRegs=new ArrayList<>();


    public HashSet<LGlobalReg>globalRegs=new HashSet<>();
    public HashMap<LGlobalReg,String>strings=new HashMap<>();

    public HashSet<LFunction> functions=new HashSet<>();
    public HashSet<LFunction> builtinFunctions=new HashSet<>();


    public LRoot(){
        for(int i=0; i<32; i++){
            realRegs.add(new RealReg(RegName.get(i)));
            if(RegPrivilege.get(i)==1)
                callerRegs.add(realRegs.get(i));
            else if(RegPrivilege.get(i)==2)
                calleeRegs.add(realRegs.get(i));
        }
        assignRegs.addAll(callerRegs);
        assignRegs.addAll(calleeRegs);
        assignRegs.remove(0);
        assignRegs.add(realRegs.get(1));//Why
    }

}
