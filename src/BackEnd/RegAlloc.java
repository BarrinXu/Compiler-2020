package BackEnd;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.RealReg;
import assembly.LOperand.VirReg;
import assembly.LRoot;
import assembly.RISCVInst.*;

import static java.lang.Math.max;

public class RegAlloc {
    LRoot root;
    public RealReg sp,t0,t1,t2;
    public RegAlloc(LRoot root){
        this.root=root;
        sp=root.realRegs.get(2);
        t0=root.realRegs.get(5);
        t1=root.realRegs.get(6);
        t2=root.realRegs.get(7);
    }
    public int maxStack=0;
    public LFunction nowFunc;
    public BaseInst loadVirReg(VirReg reg, RealReg rd, LIRBlock block){
        maxStack=max(maxStack,reg.index*4+4+nowFunc.parametersOffset);
        return new Ld(rd,block,sp,new Imm(reg.index*4+nowFunc.parametersOffset),4);
    }
    public BaseInst storeVirReg(VirReg reg,LIRBlock block){
        maxStack=max(maxStack,reg.index*4+4+nowFunc.parametersOffset);
        return new St(block,sp,new Imm(reg.index*4+nowFunc.parametersOffset),t2,4);
    }
    public void solve(){
        root.functions.forEach(this::solveFunc);
    }
    public void solveFunc(LFunction func){
        maxStack=0;
        nowFunc=func;
        func.blocks.forEach(block -> {
            for(BaseInst inst=block.head; inst!=null; inst=inst.nxt){
                if(inst instanceof Bz){
                    if(((Bz) inst).branchResult instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((Bz) inst).branchResult,t0,block));
                        ((Bz) inst).branchResult=t0;
                    }
                }
                else if(inst instanceof IType){
                    if(((IType) inst).rs1 instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((IType) inst).rs1,t0,block));
                        ((IType) inst).rs1=t0;
                    }
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof La){
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof Ld){
                    if(((Ld) inst).address instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((Ld) inst).address,t0,block));
                        ((Ld) inst).address=t0;
                    }
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof Li){
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof Lui){
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof Mv){
                    if(((Mv) inst).ori  instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((Mv) inst).ori,t0,block));
                        ((Mv) inst).ori=t0;
                    }
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof RType){
                    if(((RType) inst).rs1 instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((RType) inst).rs1,t0,block));
                        ((RType) inst).rs1=t0;
                    }
                    if(((RType) inst).rs2 instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((RType) inst).rs2,t1,block));
                        ((RType) inst).rs2=t1;
                    }
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
                else if(inst instanceof St){
                    if(((St) inst).val instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((St) inst).val,t0,block));
                        ((St) inst).val=t0;
                    }
                    if(((St) inst).address instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((St) inst).address,t1,block));
                        ((St) inst).address=t1;
                    }
                }
                else if(inst instanceof Sz){
                    if(((Sz) inst).rs1 instanceof VirReg){
                        block.addBefore(inst,loadVirReg((VirReg) ((Sz) inst).rs1,t0,block));
                        ((Sz) inst).rs1=t0;
                    }
                    if(inst.dest instanceof VirReg){
                        block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        inst.dest=t2;
                    }
                }
            }
        });
        func.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                inst.addStackSize(maxStack);
        });
    }
}
