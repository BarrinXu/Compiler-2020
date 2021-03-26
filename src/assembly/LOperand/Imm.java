package assembly.LOperand;

public class Imm extends LOperand{
    public int val;
    public Imm(int val){
        this.val=val;
    }

    @Override
    public String toString() {
        return val+"";
    }
}
