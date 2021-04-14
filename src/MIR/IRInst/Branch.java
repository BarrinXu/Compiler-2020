package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Branch extends Inst{
    public IRBaseOperand condition;
    public IRBlock thenDestBlock,elseDestBlock;

    public Branch(IRBlock block, IRBaseOperand condition,IRBlock thenDestBlock,IRBlock elseDestBlock) {
        super(null,block);
        this.condition=condition;
        this.thenDestBlock=thenDestBlock;
        this.elseDestBlock=elseDestBlock;
        condition.addUsedInst(this);
    }


    @Override
    public void remove(boolean delete) {
        if(delete)
            block.deleteTerminate();
        condition.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(condition==oriReg)
            condition=to;
    }
}
