package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.ArrayList;

public class Phi extends Inst{
    public ArrayList<IRBlock> blocks;
    public ArrayList<IRBaseOperand> operands;


    public Phi(Register reg, IRBlock block,ArrayList<IRBlock>blocks,ArrayList<IRBaseOperand>operands) {
        super(reg, block);
        this.blocks=blocks;
        this.operands=operands;
        operands.forEach(operand->operand.addUsedInst(this));
        reg.defInst=this;
    }

    public void deleteBlock(IRBlock block){
        int size=blocks.size();
        for(int i=0; i<size; i++)
            if(blocks.get(i)==block){
                blocks.remove(i);
                operands.remove(i);
                break;
            }
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        operands.forEach(operand -> {
            operand.removeUsedInst(this);
        });
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        int size=operands.size();
        for(int i=0; i<size; i++)
            if(operands.get(i)==oriReg)
                operands.set(i,to);
    }
}
