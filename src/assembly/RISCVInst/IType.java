package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.RealReg;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;

import static java.lang.Math.abs;

public class IType extends BaseInst{
    public Reg rs1;
    public Imm imm;
    public CalOpType opCode;

    public IType(Reg dest, LIRBlock LBlock,Reg rs1,Imm imm,CalOpType opCode) {
        super(dest, LBlock);
        this.rs1=rs1;
        this.imm=imm;
        this.opCode=opCode;
    }
    @Override
    public String toString(){
        if(abs(imm.val)>imm.limit){
            assert opCode==CalOpType.add;
            String tmp=imm.workOverLimit(dest,rs1).toString();
            return tmp+"\t"+opCode+"i "+dest+", "+dest+", "+imm.val;
        }
        return opCode+"i "+dest+", "+rs1+", "+imm.val;
    }

    @Override
    public void addStackSize(int stackSize) {
        if(imm instanceof StackImm)
            imm=new Imm(stackSize*(((StackImm) imm).reverse?-1:1)+ imm.val);
    }
}
