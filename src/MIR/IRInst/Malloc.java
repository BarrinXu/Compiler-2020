package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Malloc extends Inst{
    public IRBaseOperand size;

    public Malloc(Register reg, IRBlock block, IRBaseOperand size) {
        super(reg, block);
        this.size=size;
        size.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        size.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(size==oriReg)
            size=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new Malloc((Register) getMirOperand(reg,mirOperands),newBlock,getMirOperand(size,mirOperands)));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(size);
        return tmp;
    }

    @Override
    public boolean same(Inst inst) {
        return false;
    }
}
