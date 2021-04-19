package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Load extends Inst{
    public IRBaseOperand address;
    public Load(Register reg,IRBaseOperand address,IRBlock block) {
        super(reg, block);
        this.address=address;
        address.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        address.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(address==oriReg)
            address=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new Load((Register) getMirOperand(reg,mirOperands),getMirOperand(address,mirOperands),newBlock));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(address);
        return tmp;
    }
}
