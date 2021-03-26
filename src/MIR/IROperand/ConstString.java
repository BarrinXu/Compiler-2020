package MIR.IROperand;

import MIR.IRInst.Inst;
import MIR.IRType.ArrayType;
import MIR.IRType.IntType;
import MIR.IRType.PointerType;

public class ConstString extends IRBaseOperand{
    public String name;
    public String val;

    public ConstString(String name,String val) {
        super(new PointerType(new ArrayType(val.length(),new IntType(8)),true));
        this.name=name;
        this.val=val;
    }

    public String extract(){
        String tmp=val;
        tmp = val.replace("\\", "\\5C");

        tmp = tmp.replace("\0", "\\00");
        tmp = tmp.replace("\n", "\\0A");
        tmp = tmp.replace("\t", "\\09");

        tmp = tmp.replace("\"", "\\22");
        return tmp;
    }

    @Override
    public void addUsedInst(Inst inst) {

    }

    @Override
    public void removeUsedInst(Inst inst) {

    }

    @Override
    public String getIdentity() {
        return "@"+name;
    }
}
