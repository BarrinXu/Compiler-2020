package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;
import java.util.HashSet;

public class Binary extends Inst{
    public enum BinaryOpType{
        mul,sdiv,srem,and,xor,shl,ashr,or,sub,add
    }
    public BinaryOpType opCode;
    public IRBaseOperand lhs,rhs;
    public boolean canReverse;
    public Binary(Register reg, IRBlock block,BinaryOpType opCode,IRBaseOperand lhs,IRBaseOperand rhs) {
        super(reg, block);
        this.opCode=opCode;
        this.lhs=lhs;
        this.rhs=rhs;
        canReverse=opCode==BinaryOpType.mul||opCode==BinaryOpType.and||opCode==BinaryOpType.xor||opCode==BinaryOpType.or||opCode==BinaryOpType.add;
        lhs.addUsedInst(this);
        rhs.addUsedInst(this);
        reg.defInst=this;
    }
    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        lhs.removeUsedInst(this);
        rhs.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(lhs==oriReg)
            lhs=to;
        if(rhs==oriReg)
            rhs=to;
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new Binary((Register) getMirOperand(reg,mirOperands),newBlock,opCode,getMirOperand(lhs,mirOperands),getMirOperand(rhs,mirOperands)));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(lhs);
        tmp.add(rhs);
        return tmp;
    }

    @Override
    public boolean same(Inst inst) {
        if(inst instanceof Binary){
            if(opCode==((Binary) inst).opCode){
                if(canReverse)
                    return (lhs.equals(((Binary) inst).lhs)&&rhs.equals(((Binary) inst).rhs))||(lhs.equals(((Binary) inst).rhs)&&rhs.equals(((Binary) inst).lhs));
                else
                    return lhs.equals(((Binary) inst).lhs)&&rhs.equals(((Binary) inst).rhs);
            }
        }
        return false;
    }
}
