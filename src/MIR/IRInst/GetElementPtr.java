package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.ConstInt;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;
import MIR.IRType.IRBaseType;

public class GetElementPtr extends Inst{
    public IRBaseType type;
    public IRBaseOperand pointer, arrayOffset;
    public ConstInt elementOffset;


    public GetElementPtr(Register reg, IRBlock block, IRBaseType type, IRBaseOperand pointer, IRBaseOperand arrayOffset, ConstInt elementOffset) {
        super(reg, block);
        this.type=type;
        this.pointer=pointer;
        this.arrayOffset=arrayOffset;
        this.elementOffset=elementOffset;
        pointer.addUsedInst(this);
        arrayOffset.addUsedInst(this);
        reg.defInst=this;
    }

    @Override
    public void remove() {
        block.removeInst(this);
        pointer.removeUsedInst(this);
        arrayOffset.removeUsedInst(this);
        if(elementOffset!=null)
            elementOffset.removeUsedInst(this);
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        if(pointer==oriReg)
            pointer=to;
        if(arrayOffset==oriReg)
            arrayOffset=to;
    }
}
