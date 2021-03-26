package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

public class Binary extends Inst{
    public enum BinaryOpType{
        mul,sdiv,srem,and,xor,shl,ashr,or,sub,add
    }
    public BinaryOpType opCode;
    public IRBaseOperand lhs,rhs;
    public Binary(Register reg, IRBlock block,BinaryOpType opCode,IRBaseOperand lhs,IRBaseOperand rhs) {
        super(reg, block);
        this.opCode=opCode;
        this.lhs=lhs;
        this.rhs=rhs;
        lhs.addUsedInst(this);
        rhs.addUsedInst(this);
        reg.defInst=this;
    }
    @Override
    public void remove() {
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
}