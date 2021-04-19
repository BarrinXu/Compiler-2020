package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Store extends Inst{
    public IRBaseOperand val, address;

    public Store(IRBaseOperand address, IRBaseOperand val, IRBlock block) {
        super(null, block);
        this.val=val;
        this.address=address;
        this.val.addUsedInst(this);
        this.address.addUsedInst(this);
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        val.removeUsedInst(this);
        address.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(val==oriReg)
            val=to;
        if(address==oriReg)
            address=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new Store(getMirOperand(address,mirOperands),getMirOperand(val,mirOperands),newBlock));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(val);
        tmp.add(address);
        return tmp;
    }
}
