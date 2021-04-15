package Optimize;

import BackEnd.DomAnalysis;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Binary;
import MIR.IRInst.Branch;
import MIR.IRInst.Cmp;
import MIR.IRInst.Jump;
import MIR.IROperand.*;
import MIR.Root;
import com.sun.jdi.InternalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ConstantPropagation {
    public Root IRRoot;
    public boolean success,changed;
    public Function nowFunc;

    public HashMap<IRBaseOperand,IRBaseOperand>constMap=new HashMap<>();
    public HashSet<IRBlock> visitBlocks=new HashSet<>();
    public HashSet<IRBlock> reachBlocks=new HashSet<>();


    public ConstantPropagation(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public boolean solve(){
        success=false;
        constMap=new HashMap<>();
        IRRoot.functions.forEach((name,func)->solveFunc(func));
        return success;
    }
    public boolean isConst(IRBaseOperand operand){
        return operand instanceof ConstInt||operand instanceof ConstBool||operand instanceof ConstString;
    }
    public void blockDFS(IRBlock block){
        block.sons.forEach(son->{
            if(!reachBlocks.contains(son)){
                reachBlocks.add(son);
                blockDFS(son);
            }
        });
    }

    public void reachAnalysis(Function func){
        reachBlocks=new HashSet<>();
        reachBlocks.add(func.inBlock);
        blockDFS(func.inBlock);
    }

    public void solveFunc(Function func){
        nowFunc=func;
        changed=true;
        boolean needUpd=false;
        while(changed){
            changed=false;
            visitBlocks.clear();
            solveBlock(func.inBlock);
            reachAnalysis(func);
            func.blocks.forEach(block -> {
                if(!reachBlocks.contains(block)){
                    block.deleteTerminate();
                    block.setTerminate(new Jump(block,block));//why??
                }
            });
            success|=changed;
            needUpd|=changed;
        }
        if(needUpd)
            new DomAnalysis(func).solve();
    }
    public void solveBlock(IRBlock block){
        visitBlocks.add(block);
        //To be done
        for(var inst=block.head; inst!=null; inst=inst.nxt){
            //To be done
            if(inst== block.tail&&inst instanceof Branch){
                if(isConst(((Branch) inst).condition)){
                    if(!(((Branch) inst).condition instanceof ConstBool))
                        throw new InternalException();
                    changed=true;
                    block.deleteTerminate();
                    if(((ConstBool) ((Branch) inst).condition).val==true)
                        block.setTerminate(new Jump(block,((Branch) inst).thenDestBlock));
                    else
                        block.setTerminate(new Jump(block,((Branch) inst).elseDestBlock));
                }
            }
            else if((inst instanceof Binary&&binaryCheckAndReset((Binary) inst))||(inst instanceof Cmp&&cmpCheckAndReset((Cmp) inst))){
                changed=true;
                inst.remove(true);
            }
        }
        for(var son:block.sons){
            if(!visitBlocks.contains(son))
                solveBlock(son);
        }
    }
    public IRBaseOperand transToConst(IRBaseOperand operand){
        if(operand instanceof ConstInt||operand instanceof ConstBool||operand instanceof ConstString)
            return operand;
        if(operand instanceof Null)
            return new ConstInt(0,32);
        else
            return constMap.getOrDefault(operand,null);
    }
    public boolean binaryCheckAndReset(Binary inst){
        IRBaseOperand rs1=transToConst(inst.lhs),rs2=transToConst(inst.rhs);
        if(rs1!=null&&rs2!=null){
            if(rs1 instanceof ConstInt&&rs2 instanceof ConstInt){
                int ret;
                switch (inst.opCode){
                    case mul -> ret= ((ConstInt) rs1).val* ((ConstInt) rs2).val;
                    case sdiv -> {
                        if(((ConstInt) rs2).val==0)
                            return false;
                        ret= ((ConstInt) rs1).val/ ((ConstInt) rs2).val;
                    }
                    case srem -> {
                        if(((ConstInt) rs2).val==0)
                            return false;
                        ret= ((ConstInt) rs1).val% ((ConstInt) rs2).val;
                    }
                    case and -> ret= ((ConstInt) rs1).val& ((ConstInt) rs2).val;
                    case xor -> ret= ((ConstInt) rs1).val^ ((ConstInt) rs2).val;
                    case shl -> ret= ((ConstInt) rs1).val<< ((ConstInt) rs2).val;
                    case ashr -> ret= ((ConstInt) rs1).val>> ((ConstInt) rs2).val;
                    case or -> ret= ((ConstInt) rs1).val| ((ConstInt) rs2).val;
                    case sub -> ret= ((ConstInt) rs1).val- ((ConstInt) rs2).val;
                    case add -> ret= ((ConstInt) rs1).val+ ((ConstInt) rs2).val;
                    default -> throw new InternalException();
                }
                var replacedDest=new ConstInt(ret,32);
                inst.reg.replaceUse(replacedDest);
                constMap.put(inst.reg,replacedDest);
                return true;
            }
            else if(rs1 instanceof ConstBool&&rs2 instanceof ConstBool){
                boolean ret;
                switch (inst.opCode){
                    case and -> ret= ((ConstBool) rs1).val& ((ConstBool) rs2).val;
                    case xor -> ret= ((ConstBool) rs1).val^ ((ConstBool) rs2).val;
                    case or -> ret= ((ConstBool) rs1).val| ((ConstBool) rs2).val;
                    default -> throw new InternalException();
                }
                var replacedDest=new ConstBool(ret);
                inst.reg.replaceUse(replacedDest);
                constMap.put(inst.reg,replacedDest);//Experimental
                return true;
            }
            return false;
        }
        else
            return false;
    }
    public boolean cmpCheckAndReset(Cmp inst){
        IRBaseOperand rs1=transToConst(inst.lhs),rs2=transToConst(inst.rhs);
        if(rs1!=null&&rs2!=null){
            boolean ret;
            if(rs1 instanceof ConstInt&&rs2 instanceof ConstInt){
                switch (inst.opCode){
                    case slt -> ret=((ConstInt) rs1).val< ((ConstInt) rs2).val;
                    case sle -> ret=((ConstInt) rs1).val<= ((ConstInt) rs2).val;
                    case sgt -> ret=((ConstInt) rs1).val> ((ConstInt) rs2).val;
                    case sge -> ret=((ConstInt) rs1).val>= ((ConstInt) rs2).val;
                    case eq -> ret=((ConstInt) rs1).val== ((ConstInt) rs2).val;
                    case ne -> ret=((ConstInt) rs1).val!= ((ConstInt) rs2).val;
                    default -> throw new InternalException();
                }
            }
            else if(rs1 instanceof ConstBool&&rs2 instanceof ConstBool){
                switch (inst.opCode){
                    case eq -> ret=((ConstBool) rs1).val== ((ConstBool) rs2).val;
                    case ne -> ret= ((ConstBool) rs1).val!= ((ConstBool) rs2).val;
                    default -> throw new InternalException("Error CmpOpcode");
                }
            }
            else
                return false;
            var replacedDest=new ConstBool(ret);
            inst.reg.replaceUse(replacedDest);
            constMap.put(inst.reg,replacedDest);//Experimental
            return true;
        }
        else
            return false;
    }
}
