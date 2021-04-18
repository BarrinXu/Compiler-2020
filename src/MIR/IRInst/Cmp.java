package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.HashMap;

public class Cmp extends Inst{
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
        newBlock.pushInst(new Cmp((Register) getMirOperand(reg,mirOperands),newBlock,opCode,getMirOperand(lhs,mirOperands),getMirOperand(rhs,mirOperands)));
    }

    public enum CmpOpType{
        slt,sle,sgt,sge,eq,ne
    }
    public CmpOpType opCode, opposite_opCode;
    public IRBaseOperand lhs,rhs;

    public Cmp(Register reg, IRBlock block,CmpOpType opCode,IRBaseOperand lhs,IRBaseOperand rhs) {
        super(reg, block);
        this.opCode=opCode;
        this.lhs=lhs;
        this.rhs=rhs;
        lhs.addUsedInst(this);
        rhs.addUsedInst(this);
        reg.defInst=this;
        if(opCode==CmpOpType.slt)
            opposite_opCode=CmpOpType.sgt;
        else if(opCode==CmpOpType.sle)
            opposite_opCode=CmpOpType.sge;
        else if(opCode==CmpOpType.sgt)
            opposite_opCode=CmpOpType.slt;
        else if(opCode==CmpOpType.sge)
            opposite_opCode=CmpOpType.sle;
        else if(opCode==CmpOpType.eq)
            opposite_opCode=CmpOpType.ne;
        else if(opCode==CmpOpType.ne)
            opposite_opCode=CmpOpType.eq;

    }
}
