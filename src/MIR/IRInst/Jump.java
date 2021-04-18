package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;

public class Jump extends Inst{
    public IRBlock destBlock;
    public Jump(IRBlock block, IRBlock destBlock) {
        super(null, block);
        this.destBlock=destBlock;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.deleteTerminate();
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {

    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.setTerminate(new Jump(newBlock,mirBlocks.get(destBlock)));
    }
}
