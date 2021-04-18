package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.*;
import MIR.IROperand.*;
import MIR.IRType.ClassType;
import MIR.IRType.PointerType;
import MIR.Root;
import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.*;
import assembly.LRoot;
import assembly.RISCVInst.*;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class InstSelection {
    public Root IRRoot;
    public LRoot LRoot=new LRoot();


    public int cnt=0;
    public HashMap<IRBaseOperand,Reg>regMap=new HashMap<>();
    public HashMap<Function,LFunction>funcMap=new HashMap<>();
    public HashMap<IRBlock, LIRBlock>blockMap=new HashMap<>();

    public HashMap<Integer,Reg>UsedConstReg=new HashMap<>();

    public LFunction nowLFunction;
    public LIRBlock nowLBlock;

    public InstSelection(Root IRRoot){
        this.IRRoot=IRRoot;
    }

    public Reg MirToLir(IRBaseOperand operand){
        if(operand instanceof Register||operand instanceof Parameter){
            if(!regMap.containsKey(operand))
                regMap.put(operand,new VirReg(cnt++,operand.type.size()/8));
            return regMap.get(operand);
        }
        else if(operand instanceof GlobalRegister){
            if(!regMap.containsKey(operand)){
                int size;
                if(operand.type.needPtrLoad())
                    size=((PointerType)operand.type).dest.size();
                else
                    size=operand.type.size();
                LGlobalReg reg=new LGlobalReg(((GlobalRegister) operand).name,size/8);
                regMap.put(operand,reg);
                LRoot.globalRegs.add(reg);
            }
            return regMap.get(operand);
        }
        else if(operand instanceof ConstString){
            if(!regMap.containsKey(operand)){
                int size;
                if(operand.type.needPtrLoad())
                    size=((PointerType)operand.type).dest.size();
                else
                    size=operand.type.size();
                LGlobalReg reg=new LGlobalReg("."+((ConstString) operand).name,size/8);
                regMap.put(operand,reg);
                LRoot.strings.put(reg,((ConstString) operand).val);
            }
            return regMap.get(operand);
        }
        else if(operand instanceof ConstInt){
            int val=((ConstInt) operand).val;
            if(val==0)
                return LRoot.realRegs.get(0);
            if(UsedConstReg.containsKey(val))
                return UsedConstReg.get(val);
            VirReg reg=new VirReg(cnt++,4);
            UsedConstReg.put(val,reg);
            nowLBlock.pushInst(new Li(reg,nowLBlock,new Imm(val)));
            return reg;
        }
        else if(operand instanceof ConstBool){
            int val=((ConstBool) operand).val?1:0;
            if(val==0)
                return LRoot.realRegs.get(0);
            if(UsedConstReg.containsKey(val))
                return UsedConstReg.get(val);
            VirReg reg=new VirReg(cnt++,1);
            UsedConstReg.put(val,reg);
            nowLBlock.pushInst(new Li(reg,nowLBlock,new Imm(val)));
            return reg;
        }
        else
            return LRoot.realRegs.get(0);
    }

    public LRoot solve(){
        IRRoot.builtInFunctions.forEach((name,func)->{
            LFunction LFunc=new LFunction(name,null,null);
            LRoot.builtinFunctions.add(LFunc);
            funcMap.put(func,LFunc);
        });
        IRRoot.functions.forEach((name,func)->{
            func.blocks.forEach(block -> {
                blockMap.put(block,new LIRBlock("."+func.name+"_"+block.name));
            });
            LFunction Lfunc=new LFunction(name,blockMap.get(func.inBlock),blockMap.get(func.outBlock));
            funcMap.put(func,Lfunc);
            func.parameters.forEach(parameter -> {
                Lfunc.parameters.add(MirToLir(parameter));
            });
            LRoot.functions.add(Lfunc);
        });
        IRRoot.functions.forEach((name,func)->{
            solveFunc(func);
        });
        return LRoot;
    }

    public void solveFunc(Function func){
        cnt=0;
        LFunction Lfunc=funcMap.get(func);
        func.parameters.forEach(parameter -> {
            Lfunc.parameters.add(MirToLir(parameter));
        });
        LIRBlock inBlock=Lfunc.inBlock,outBlock=Lfunc.outBlock;
        nowLFunction=Lfunc;
        ArrayList<VirReg>CalleeVirRegLists=new ArrayList<>();

        StackImm stackImm=new StackImm(0);
        stackImm.reverse=true;
        inBlock.pushInst(new IType(LRoot.realRegs.get(2),inBlock,LRoot.realRegs.get(2),stackImm, BaseInst.CalOpType.add));



        LRoot.calleeRegs.forEach(reg->{
            VirReg tmp=new VirReg(cnt++,4);
            CalleeVirRegLists.add(tmp);
            inBlock.pushInst(new Mv(tmp,inBlock,reg));
        });

        VirReg x1_backup =new VirReg(cnt++,4);
        inBlock.pushInst(new Mv(x1_backup,inBlock,LRoot.realRegs.get(1)));

        for(int i=0; i<min(8,func.parameters.size()); i++)
            inBlock.pushInst(new Mv(Lfunc.parameters.get(i),inBlock,LRoot.realRegs.get(10+i)));

        int offset =0;
        for(int i=8; i<func.parameters.size(); i++){
            inBlock.pushInst(new Ld(Lfunc.parameters.get(i),inBlock,LRoot.realRegs.get(2),new StackImm(offset),func.parameters.get(i).type.size()/8));
            offset +=4;//Why
        }

        func.blocks.forEach(block -> {
            LIRBlock Lblock=blockMap.get(block);
            UsedConstReg.clear();
            solveBlock(block,Lblock);
            Lfunc.blocks.add(Lblock);
        });

        for(int i=0; i<LRoot.calleeRegs.size(); i++)
            outBlock.pushInst(new Mv(LRoot.calleeRegs.get(i),outBlock,CalleeVirRegLists.get(i)));
        outBlock.pushInst(new Mv(LRoot.realRegs.get(1),outBlock,x1_backup));
        outBlock.pushInst(new IType(LRoot.realRegs.get(2),outBlock,LRoot.realRegs.get(2),new StackImm(0), BaseInst.CalOpType.add));
        outBlock.pushInst(new Ret(outBlock,LRoot));
        Lfunc.regCnt=cnt;
    }
    public void solveBlock(IRBlock oriBlock,LIRBlock LBlock){
        nowLBlock=LBlock;
        for(Inst inst= oriBlock.head;inst!=null;inst=inst.nxt)
            solveInst(inst);
        oriBlock.sons.forEach(son->{
            LBlock.sons.add(blockMap.get(son));
            blockMap.get(son).fas.add(LBlock);
        });
    }
    public boolean inLimit(int val){
        return (val<(1<<11))&&(val>(-1*(1<<11)));//maybe can to -2048?
    }
    public boolean judgeImm(IRBaseOperand operand){
        return operand instanceof ConstInt&&inLimit(((ConstInt) operand).val);
    }
    public boolean canReverse(BaseInst.CalOpType opCode){
        return opCode== BaseInst.CalOpType.add||opCode== BaseInst.CalOpType.mul||opCode== BaseInst.CalOpType.and||opCode== BaseInst.CalOpType.or||opCode== BaseInst.CalOpType.xor;
    }
    public void solveInst(Inst inst){
        LIRBlock block=nowLBlock;
        if(inst instanceof Binary){
            BaseInst.CalOpType opCode=null;
            switch (((Binary) inst).opCode){
                case mul -> opCode= BaseInst.CalOpType.mul;
                case sdiv -> opCode= BaseInst.CalOpType.div;
                case srem -> opCode= BaseInst.CalOpType.rem;
                case and -> opCode= BaseInst.CalOpType.and;
                case xor -> opCode= BaseInst.CalOpType.xor;
                case shl -> opCode= BaseInst.CalOpType.sll;
                case ashr -> opCode= BaseInst.CalOpType.sra;
                case or -> opCode= BaseInst.CalOpType.or;
                case sub -> opCode= BaseInst.CalOpType.sub;
                case add -> opCode= BaseInst.CalOpType.add;
            }
            /*if(opCode==BaseInst.CalOpType.mul||opCode==BaseInst.CalOpType.div||opCode==BaseInst.CalOpType.rem)
            {
                block.pushInst(new RType(MirToLir(inst.reg),block,MirToLir(((Binary) inst).lhs),MirToLir(((Binary) inst).rhs),opCode));
                return;
            }
            else{
                if(canReverse(opCode)&&judgeImm(((Binary) inst).lhs)){
                    block.pushInst(new IType(MirToLir(inst.reg),block,MirToLir(((Binary) inst).rhs),new Imm(((ConstInt)((Binary) inst).lhs).val),opCode));
                    return;
                }
                if(judgeImm(((Binary) inst).rhs)){
                    if(opCode!= BaseInst.CalOpType.sub)
                        block.pushInst(new IType(MirToLir(inst.reg),block,MirToLir(((Binary) inst).lhs),new Imm(((ConstInt)((Binary) inst).rhs).val),opCode));
                    else
                        block.pushInst(new IType(MirToLir(inst.reg),block,MirToLir(((Binary) inst).lhs),new Imm(-1*((ConstInt)((Binary) inst).rhs).val), BaseInst.CalOpType.add));
                    return;
                }
            }*/
            block.pushInst(new RType(MirToLir(inst.reg),block,MirToLir(((Binary) inst).lhs),MirToLir(((Binary) inst).rhs),opCode));
        }
        else if(inst instanceof BitCast){
            Reg oriReg=MirToLir(((BitCast) inst).it);
            if(oriReg instanceof LGlobalReg)
                //Why
                block.pushInst(new La(MirToLir(inst.reg),block, (LGlobalReg) oriReg));
            else
                block.pushInst(new Mv(MirToLir(inst.reg),block,oriReg));
        }
        else if(inst instanceof Branch){
            block.pushInst(new Bz(block,MirToLir(((Branch) inst).condition), BaseInst.CmpOpType.beq,blockMap.get(((Branch) inst).elseDestBlock)));
            block.pushInst(new Jp(block,blockMap.get(((Branch) inst).thenDestBlock)));
        }
        else if(inst instanceof Cmp){
            switch (((Cmp) inst).opCode){
                case slt -> block.pushInst(new RType(MirToLir(inst.reg),block,MirToLir(((Cmp) inst).lhs),MirToLir(((Cmp) inst).rhs), BaseInst.CalOpType.slt));
                case sle -> {
                    VirReg tmp=new VirReg(cnt++,4);
                    block.pushInst(new RType(tmp,block,MirToLir(((Cmp) inst).rhs),MirToLir(((Cmp) inst).lhs), BaseInst.CalOpType.slt));
                    block.pushInst(new IType(MirToLir(inst.reg),block,tmp,new Imm(1), BaseInst.CalOpType.xor));
                }
                case sgt -> block.pushInst(new RType(MirToLir(inst.reg),block,MirToLir(((Cmp) inst).rhs),MirToLir(((Cmp) inst).lhs), BaseInst.CalOpType.slt));
                case sge -> {
                    VirReg tmp=new VirReg(cnt++,4);
                    block.pushInst(new RType(tmp,block,MirToLir(((Cmp) inst).lhs),MirToLir(((Cmp) inst).rhs), BaseInst.CalOpType.slt));
                    block.pushInst(new IType(MirToLir(inst.reg),block,tmp,new Imm(1), BaseInst.CalOpType.xor));
                }
                case eq -> {
                    VirReg tmp=new VirReg(cnt++,4);
                    block.pushInst(new RType(tmp,block,MirToLir(((Cmp) inst).lhs),MirToLir(((Cmp) inst).rhs), BaseInst.CalOpType.xor));
                    block.pushInst(new Sz(MirToLir(inst.reg),block,tmp, BaseInst.CmpOpType.beq));
                }
                case ne -> {
                    VirReg tmp=new VirReg(cnt++,4);
                    block.pushInst(new RType(tmp,block,MirToLir(((Cmp) inst).lhs),MirToLir(((Cmp) inst).rhs), BaseInst.CalOpType.xor));
                    block.pushInst(new Sz(MirToLir(inst.reg),block,tmp, BaseInst.CmpOpType.bne));
                }
            }
        }
        else if(inst instanceof Call){
            for(int i=0; i<min(((Call) inst).parameters.size(),8); i++){
                //Assume it can not be global reg
                block.pushInst(new Mv(LRoot.realRegs.get(10+i),block,MirToLir(((Call) inst).parameters.get(i))));
            }
            int offset=0;
            for(int i=8; i<((Call) inst).parameters.size(); i++){
                block.pushInst(new St(block,LRoot.realRegs.get(2),new Imm(offset),MirToLir(((Call) inst).parameters.get(i)),((Call) inst).parameters.get(i).type.size()/8));
                offset+=4;
            }
            nowLFunction.parametersOffset=max(nowLFunction.parametersOffset, offset);
            block.pushInst(new Ca(block,funcMap.get(((Call) inst).func),LRoot));
            if(inst.reg!=null)
                block.pushInst(new Mv(MirToLir(inst.reg),block,LRoot.realRegs.get(10)));
        }
        else if(inst instanceof Jump)
            block.pushInst(new Jp(block,blockMap.get(((Jump) inst).destBlock)));
        else if(inst instanceof Load)
            block.pushInst(new Ld(MirToLir(inst.reg),block,MirToLir(((Load) inst).address),new Imm(0),inst.reg.type.size()/8));
        else if(inst instanceof Store){
            Reg transAddress=MirToLir(((Store) inst).address);
            if(transAddress instanceof LGlobalReg){
                VirReg highAddress=new VirReg(cnt++,4);
                BitCut highBit=new BitCut((LGlobalReg) transAddress,true);
                block.pushInst(new Lui(highAddress,block,highBit));
                block.pushInst(new St(block,highAddress,new BitCut((LGlobalReg) transAddress,false),MirToLir(((Store) inst).val),((Store) inst).val.type.size()/8));
            }
            else
                block.pushInst(new St(block,transAddress,new Imm(0),MirToLir(((Store) inst).val),((Store) inst).val.type.size()/8));
        }
        else if(inst instanceof Return){
            if(((Return) inst).val!=null){
                Reg transReg=MirToLir(((Return) inst).val);
                if(transReg instanceof LGlobalReg) {
                    block.pushInst(new La(LRoot.realRegs.get(10),block, (LGlobalReg) transReg)); }
                else
                    block.pushInst(new Mv(LRoot.realRegs.get(10),block,transReg));
            }
        }
        else if(inst instanceof Move){
            if(((Move) inst).oriOperand instanceof GlobalRegister||((Move) inst).oriOperand instanceof ConstString)
                block.pushInst(new La(MirToLir(inst.reg),block, (LGlobalReg) MirToLir(((Move) inst).oriOperand)));
            else
                block.pushInst(new Mv(MirToLir(inst.reg),block,MirToLir(((Move) inst).oriOperand)));
        }
        else if(inst instanceof Malloc){
            block.pushInst(new Mv(LRoot.realRegs.get(10),block,MirToLir(((Malloc) inst).size)));
            block.pushInst(new Ca(block,funcMap.get(IRRoot.getFunction("malloc")),LRoot));
            block.pushInst(new Mv(MirToLir(inst.reg),block,LRoot.realRegs.get(10)));
        }
        else if(inst instanceof Zext){
            Reg transReg=MirToLir(((Zext) inst).origin);
            if(transReg instanceof LGlobalReg)
                block.pushInst(new La(MirToLir(inst.reg),block, (LGlobalReg) transReg));
            else
                block.pushInst(new Mv(MirToLir(inst.reg),block,transReg));
        }
        else if(inst instanceof GetElementPtr){
            VirReg destIdx=new VirReg(cnt++,4);
            VirReg destMul;
            if(((GetElementPtr) inst).arrayOffset instanceof ConstInt){
                int kth=((ConstInt) ((GetElementPtr) inst).arrayOffset).val;
                if(kth!=0){
                    block.pushInst(new RType(destIdx,block,MirToLir(((GetElementPtr) inst).pointer),MirToLir(new ConstInt(kth*((GetElementPtr) inst).type.size()/8,32)), BaseInst.CalOpType.add));
                }
                else{
                    Reg origin=MirToLir(((GetElementPtr) inst).pointer);
                    if(origin instanceof LGlobalReg)
                        block.pushInst(new La(destIdx,block, (LGlobalReg) origin));
                    else
                        block.pushInst(new Mv(destIdx,block,MirToLir(((GetElementPtr) inst).pointer)));
                }
            }
            else{
                destMul=new VirReg(cnt++,4);
                block.pushInst(new RType(destMul,block,MirToLir(((GetElementPtr) inst).arrayOffset),MirToLir(new ConstInt(((GetElementPtr) inst).type.size()/8,32)), BaseInst.CalOpType.mul));
                block.pushInst(new RType(destIdx,block,MirToLir(((GetElementPtr) inst).pointer),destMul, BaseInst.CalOpType.add));
            }
            Reg finalPtr;
            if(((GetElementPtr) inst).elementOffset==null)
                finalPtr=destIdx;
            else{
                int kth=((GetElementPtr) inst).elementOffset.val;
                if(kth==0)
                    finalPtr=destIdx;
                else{
                    assert ((GetElementPtr) inst).pointer.type instanceof PointerType;
                    int elementBitOffset=0;
                    if(((PointerType) ((GetElementPtr) inst).pointer.type).dest instanceof ClassType)
                        elementBitOffset=((ClassType) ((PointerType) ((GetElementPtr) inst).pointer.type).dest).getElementBitOffset(kth);
                    finalPtr=new VirReg(cnt++,4);
                    block.pushInst(new RType(finalPtr,block,destIdx,MirToLir(new ConstInt(elementBitOffset,32)), BaseInst.CalOpType.add));
                }
            }
            if(regMap.containsKey(inst.reg))
                block.pushInst(new Mv(regMap.get(inst.reg),block,finalPtr));
            else
                regMap.put(inst.reg,finalPtr);
        }
    }


}
