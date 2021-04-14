package assembly.LOperand;

public class VirReg extends Reg{
    public String name;
    public int size;
    public int index;
    public VirReg(int name,int size){
        super();
        index=name;
        this.name=name+"%";
        this.size=size;
    }

    @Override
    public String toString() {
        if(color==null)
            return name;
        else
            return color.toString();
    }
}
