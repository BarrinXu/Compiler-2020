package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

abstract public class Inst {

    public Register reg;
    public IRBlock block;
    public Inst lst = null, nxt = null;

    public Inst(Register reg, IRBlock block) {
        this.reg = reg;
        this.block = block;
    }

    public void deleteInList() {
        if (nxt != null)
            nxt.lst = lst;
        else
            block.tail = lst;
        if (lst != null)
            lst.nxt = nxt;
        else
            block.head = nxt;
    }

    abstract public void remove(boolean delete);

    abstract public void modifyReg(Register oriReg, IRBaseOperand to);

}
