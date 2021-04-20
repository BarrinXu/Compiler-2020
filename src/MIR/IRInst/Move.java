package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;
import com.sun.jdi.InternalException;

import java.util.HashMap;
import java.util.HashSet;

public class Move extends Inst{
    public IRBaseOperand oriOperand;

    public Move(Register reg, IRBlock block,IRBaseOperand oriOperand,boolean addUsedInst) {
        super(reg, block);
        this.oriOperand=oriOperand;
        if(addUsedInst)
            oriOperand.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        oriOperand.removeUsedInst(this);
    }

    public void modifyReg(Register oriReg, IRBaseOperand to){
        if(oriOperand==oriReg)
            oriOperand=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        throw new InternalException();
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(oriOperand);
        return tmp;
    }

    @Override
    public boolean same(Inst inst) {
        if(inst instanceof Move){
            return oriOperand.equals(((Move) inst).oriOperand);
        }
        return false;
    }
}
