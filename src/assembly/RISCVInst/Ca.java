package assembly.RISCVInst;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.Reg;
import assembly.LRoot;

import java.util.HashSet;

import static java.lang.Math.min;

public class Ca extends BaseInst{
    public LFunction func;
    public LRoot root;

    public Ca(LIRBlock LBlock,LFunction func,LRoot root) {
        super(null, LBlock);
        this.func=func;
        this.root=root;
    }
    @Override
    public String toString(){
        return "call "+func.name;
    }

    @Override
    public void addStackSize(int stackSize) {

    }

    @Override
    public HashSet<Reg> usedRegSet() {
        HashSet<Reg>tmp=new HashSet<>();
        for(int i=0; i<min(func.parameters.size(),8); i++)
            tmp.add(root.realRegs.get(10+i));
        return tmp;
    }

    @Override
    public void replaceUse(Reg origin, Reg to) {

    }
}
