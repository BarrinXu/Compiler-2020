package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Return extends Inst{
    public IRBaseOperand val;
    public Return(IRBlock block, IRBaseOperand val) {
        super(null, block);
        this.val=val;
        if(val!=null)
            val.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.deleteTerminate();
        if(val!=null)
            val.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(val==oriReg)
            val=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.setTerminate(new Return(newBlock,val==null?null:getMirOperand(val,mirOperands)));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        if(val!=null)
            tmp.add(val);
        return tmp;
    }

    @Override
    public boolean same(Inst inst) {
        return false;
    }
}
