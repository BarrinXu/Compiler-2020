package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        ArrayList<IRBaseOperand>mirValues=new ArrayList<>();
        operands.forEach(value->{
            mirValues.add(getMirOperand(value,mirOperands));
        });
        ArrayList<IRBlock>mirRelateBlocks=new ArrayList<>();
        blocks.forEach(oriBlock->{
            mirRelateBlocks.add(mirBlocks.get(oriBlock));
        });
        newBlock.add_PhiInst(new Phi((Register) getMirOperand(reg,mirOperands),newBlock,mirRelateBlocks,mirValues));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        return new HashSet<>(operands);
    }

    @Override
    public boolean same(Inst inst) {
        if(inst instanceof Phi){
            HashSet<IRBaseOperand> rhsValues=new HashSet<>(((Phi) inst).operands);
            int size=operands.size();
            if(size== rhsValues.size()){
                for(int i=0; i<size; i++){
                    var nowVal=operands.get(i);
                    if(!rhsValues.contains(nowVal)||((Phi) inst).blocks.get(((Phi) inst).operands.indexOf(nowVal))!=blocks.get(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public void addOrigin(IRBaseOperand operand,IRBlock origin){
        operands.add(operand);
        blocks.add(origin);
        operand.addUsedInst(this);
    }
}
