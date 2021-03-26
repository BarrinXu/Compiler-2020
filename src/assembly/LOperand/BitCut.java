package assembly.LOperand;

public class BitCut extends Imm{
    public LGlobalReg oriReg;
    public boolean highCut;
    public BitCut(LGlobalReg oriReg,boolean highCut){
        super(0);
        this.oriReg=oriReg;
        this.highCut=highCut;
    }
    @Override
    public String toString(){
        return "%"+(highCut?"hi":"lo")+"("+oriReg+")";
    }
}
