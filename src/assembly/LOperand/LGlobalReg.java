package assembly.LOperand;

public class LGlobalReg extends Reg{

    public String name;
    public int size;
    public LGlobalReg(String name, int size){
        super();
        this.name=name;
        this.size=size;
    }

    @Override
    public String toString() {
        return name;
    }
}
