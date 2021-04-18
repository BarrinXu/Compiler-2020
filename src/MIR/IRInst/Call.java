package MIR.IRInst;

import MIR.Function;
import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;

import java.util.ArrayList;
import java.util.HashMap;

public class Call extends Inst{
    public Function func;
    public ArrayList<IRBaseOperand> parameters;
    public Call(Function func, ArrayList<IRBaseOperand>parameters, Register reg, IRBlock block){
        super(reg,block);
        this.func=func;
        this.parameters=parameters;
        parameters.forEach(node->node.addUsedInst(this));
        if(reg!=null)
            reg.defInst=this;
    }

    @Override
    public void remove(boolean delete) {
        if(delete)
            block.removeInst(this);
        parameters.forEach(parameter->{
            parameter.removeUsedInst(this);
        });
    }

    @Override
    public void modifyReg(Register oriReg, IRBaseOperand to) {
        int size=parameters.size();
        for(int i=0; i<size; i++)
            if(parameters.get(i)==oriReg)
                parameters.set(i,to);
    }

    @Override
    public void addMirInst(IRBlock newBlock, HashMap<IRBaseOperand, IRBaseOperand> mirOperands, HashMap<IRBlock, IRBlock> mirBlocks) {
        ArrayList<IRBaseOperand>mirParameters=new ArrayList<>();
        parameters.forEach(parameter->{
            mirParameters.add(getMirOperand(parameter,mirOperands));
        });
        newBlock.pushInst(new Call(func,mirParameters,reg==null?null: (Register) getMirOperand(reg,mirOperands),newBlock));
    }
}
