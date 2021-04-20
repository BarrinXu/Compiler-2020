package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.GlobalRegister;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

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

    public abstract void remove(boolean delete);

    public abstract void modifyReg(Register oriReg, IRBaseOperand to);

    public IRBaseOperand getMirOperand(IRBaseOperand ori, HashMap<IRBaseOperand,IRBaseOperand>mirOperands){
        if(ori instanceof GlobalRegister)
            return ori;
        else if(!mirOperands.containsKey(ori)){
            mirOperands.put(ori,ori.copy());
            return mirOperands.get(ori);
        }
        else
            return mirOperands.get(ori);
    }
    public abstract void addMirInst(IRBlock newBlock,HashMap<IRBaseOperand,IRBaseOperand>mirOperands,HashMap<IRBlock,IRBlock>mirBlocks);
    public abstract HashSet<IRBaseOperand> usedOperandSet();
    public abstract boolean same(Inst inst);
}
