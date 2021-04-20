package MIR.IRInst;

import MIR.IRBlock;
import MIR.IROperand.ConstInt;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;
import MIR.IRType.IRBaseType;

import java.util.HashMap;
import java.util.HashSet;

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
    public void remove(boolean delete) {
        if(delete)
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

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        newBlock.pushInst(new GetElementPtr((Register) getMirOperand(reg,mirOperands),newBlock,type,getMirOperand(pointer,mirOperands),getMirOperand(arrayOffset,mirOperands),elementOffset));
    }

    @Override
    public HashSet<IRBaseOperand> usedOperandSet() {
        HashSet<IRBaseOperand>tmp=new HashSet<>();
        tmp.add(pointer);
        tmp.add(arrayOffset);
        return tmp;
    }

    @Override
    public boolean same(Inst inst) {
        if(inst instanceof GetElementPtr){
            return pointer.equals(((GetElementPtr) inst).pointer)&&arrayOffset.equals(((GetElementPtr) inst).arrayOffset)&&((elementOffset==null&&((GetElementPtr) inst).elementOffset==null)||(elementOffset!=null&&elementOffset.equals(((GetElementPtr) inst).elementOffset)));
        }
        return false;
    }
}
