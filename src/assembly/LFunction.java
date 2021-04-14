package assembly;

import assembly.LOperand.Reg;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class LFunction {
    public LinkedHashSet<LIRBlock> blocks=new LinkedHashSet<>();
    public ArrayList<Reg> parameters=new ArrayList<>();

    public int parametersOffset=0;
    public int regCnt;

    public String name;
    public LIRBlock inBlock,outBlock;

    public LFunction(String name,LIRBlock inBlock,LIRBlock outBlock){
        this.name=name;
        this.inBlock=inBlock;
        this.outBlock=outBlock;
    }
}
