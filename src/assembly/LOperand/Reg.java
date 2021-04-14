package assembly.LOperand;

import assembly.RISCVInst.Mv;

import java.util.HashSet;

public abstract class Reg extends LOperand{
    public int deg=0;

    public Reg alias=null;
    public RealReg color;
    public double weight=0;
    public HashSet<Reg> linkSet=new HashSet<>();
    public HashSet<Mv> moveSet=new HashSet<>();
    public Imm stackOffset=null;

    public Reg(){
        if(this instanceof RealReg)
            color= (RealReg) this;
        else
            color=null;
    }
}
