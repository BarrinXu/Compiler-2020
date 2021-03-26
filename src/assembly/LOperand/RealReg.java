package assembly.LOperand;

public class RealReg extends Reg{
    public String name;
    public RealReg(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }
}
