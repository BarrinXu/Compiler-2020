package BackEnd;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.Imm;
import assembly.LOperand.RealReg;
import assembly.LOperand.VirReg;
import assembly.LRoot;
import assembly.RISCVInst.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import static java.lang.Math.max;

public class PremiumRegAlloc {
    LRoot root;
    public RealReg sp,t0,t1,t2,t3,t4,t5,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11;//t6 is for store imm extend calculate and add-> a=b+c;
    public int cacheLimit=16;//VIP!
    public LIRBlock nowBlock;
    public BaseInst nowInst;
    public Stack<RealReg>freeCache=new Stack<>();
    public LinkedList<VirReg>CacheQueue=new LinkedList<>();
    public HashMap<VirReg,RealReg>VirRegMap=new HashMap<>();
    public HashSet<VirReg>HaveModify=new HashSet<>();
    public HashMap<LIRBlock,HashSet<VirReg>>VIP=new HashMap<>();
    public void cacheInit(){
        freeCache.clear();
        freeCache.push(t0);
        freeCache.push(t1);
        freeCache.push(t2);
        freeCache.push(t3);
        freeCache.push(t4);
        freeCache.push(t5);
        freeCache.push(s2);
        freeCache.push(s3);
        freeCache.push(s4);
        freeCache.push(s5);
        freeCache.push(s6);
        freeCache.push(s7);
        freeCache.push(s8);
        freeCache.push(s9);
        freeCache.push(s10);
        freeCache.push(s11);
        CacheQueue.clear();
        VirRegMap.clear();
    }
    public void cacheDealWithCall(){
        CacheQueue.forEach(virReg -> {
            assert VirRegMap.containsKey(virReg);
            //if(HaveModify.contains(virReg))
                nowBlock.addBefore(nowInst,storeRealReg(virReg,VirRegMap.get(virReg),nowBlock));
        });
        cacheInit();
    }
    public boolean NeedToStore(VirReg virReg){
        //return true;
        for(var iter:VIP.entrySet()){
            if(iter.getKey()!=nowBlock&&iter.getValue().contains(virReg))
                return true;
        }
        return false;
    }
    public void storeCache(){
        CacheQueue.forEach(virReg -> {
            assert VirRegMap.containsKey(virReg);
            if(HaveModify.contains(virReg)&&NeedToStore(virReg))
                nowBlock.addBefore(nowInst,storeRealReg(virReg,VirRegMap.get(virReg),nowBlock));
        });
        cacheInit();
    }

    public boolean inCache(VirReg virReg){
        return VirRegMap.containsKey(virReg);
    }
    public boolean cacheFull(){
        return VirRegMap.size()==cacheLimit;
    }
    public void removeCache(VirReg virReg){
        assert VirRegMap.containsKey(virReg);
        nowBlock.addBefore(nowInst,storeRealReg(virReg,VirRegMap.get(virReg),nowBlock));
        freeCache.push(VirRegMap.get(virReg));
        VirRegMap.remove(virReg);
        assert CacheQueue.contains(virReg);
        CacheQueue.remove(virReg);
    }
    public RealReg acquireCache(VirReg nowVirReg){
        if(cacheFull()){
            removeCache(CacheQueue.peekLast());
        }
        var dest=freeCache.pop();
        CacheQueue.push(nowVirReg);
        VirRegMap.put(nowVirReg,dest);
        return dest;
    }
    public void updateCachePriority(VirReg nowVirReg){
        assert CacheQueue.contains(nowVirReg);
        CacheQueue.remove(nowVirReg);
        CacheQueue.push(nowVirReg);
    }
    public PremiumRegAlloc(LRoot root){
        this.root=root;
        sp=root.realRegs.get(2);
        t0=root.realRegs.get(5);
        t1=root.realRegs.get(6);
        t2=root.realRegs.get(7);
        t3=root.realRegs.get(28);
        t4=root.realRegs.get(29);
        t5=root.realRegs.get(30);
        s2=root.realRegs.get(18);
        s3=root.realRegs.get(19);
        s4=root.realRegs.get(20);
        s5=root.realRegs.get(21);
        s6=root.realRegs.get(22);
        s7=root.realRegs.get(23);
        s8=root.realRegs.get(24);
        s9=root.realRegs.get(25);
        s10=root.realRegs.get(26);
        s11=root.realRegs.get(27);
    }
    public int maxStack=0;
    public LFunction nowFunc;
    public BaseInst loadVirReg(VirReg reg, LIRBlock block){
        maxStack=max(maxStack,reg.index*4+4+nowFunc.parametersOffset);
        var dest=acquireCache(reg);
        return new Ld(dest,block,sp,new Imm(reg.index*4+nowFunc.parametersOffset),4);
    }
    public BaseInst storeVirReg(VirReg reg,LIRBlock block){
        maxStack=max(maxStack,reg.index*4+4+nowFunc.parametersOffset);
        return new St(block,sp,new Imm(reg.index*4+nowFunc.parametersOffset),t2,4);
    }
    public BaseInst storeRealReg(VirReg reg,RealReg val,LIRBlock block){
        maxStack=max(maxStack,reg.index*4+4+nowFunc.parametersOffset);
        return new St(block,sp,new Imm(reg.index*4+nowFunc.parametersOffset),val,4);
    }
    public void solve(){
        root.functions.forEach(this::solveFunc);
    }
    public void solveFunc(LFunction func){
        maxStack=0;
        nowFunc=func;
        VIP.clear();
        func.blocks.forEach(block -> {
            HashSet<VirReg>virRegHashSet=new HashSet<>();
            for(BaseInst inst=block.head; inst!=null; inst=inst.nxt){
                if(inst instanceof Bz){
                    if(((Bz) inst).branchResult instanceof VirReg)
                        virRegHashSet.add((VirReg) ((Bz) inst).branchResult);
                }
                else if(inst instanceof IType){
                    if(((IType) inst).rs1 instanceof VirReg)
                        virRegHashSet.add((VirReg) ((IType) inst).rs1);
                }
                else if(inst instanceof Ld){
                    if(((Ld) inst).address instanceof VirReg)
                        virRegHashSet.add((VirReg) ((Ld) inst).address);
                }
                else if(inst instanceof Mv){
                    if(((Mv) inst).ori instanceof VirReg)
                        virRegHashSet.add((VirReg) ((Mv) inst).ori);
                }
                else if(inst instanceof RType){
                    if(((RType) inst).rs1 instanceof VirReg)
                        virRegHashSet.add((VirReg) ((RType) inst).rs1);
                    if(((RType) inst).rs2 instanceof VirReg)
                        virRegHashSet.add((VirReg) ((RType) inst).rs2);
                }
                else if(inst instanceof St){
                    if(((St) inst).val instanceof VirReg)
                        virRegHashSet.add((VirReg) ((St) inst).val);
                    if(((St) inst).address instanceof VirReg)
                        virRegHashSet.add((VirReg) ((St) inst).address);
                }
                else if(inst instanceof Sz){
                    if(((Sz) inst).rs1 instanceof VirReg)
                        virRegHashSet.add((VirReg) ((Sz) inst).rs1);
                }
            }
            VIP.put(block,virRegHashSet);
        });
        func.blocks.forEach(block -> {
            HaveModify.clear();
            cacheInit();
            nowBlock=block;
            for(BaseInst inst=block.head; inst!=null; inst=inst.nxt){
                nowInst=inst;
                if(inst instanceof Ca){
                    cacheDealWithCall();
                }
                else if(inst instanceof Bz){
                    if(((Bz) inst).branchResult instanceof VirReg){
                        if(inCache((VirReg) ((Bz) inst).branchResult)){
                            updateCachePriority((VirReg) ((Bz) inst).branchResult);
                        }
                        else{
                            block.addBefore(inst,loadVirReg((VirReg) ((Bz) inst).branchResult,block));
                        }
                        ((Bz) inst).branchResult=VirRegMap.get(((Bz) inst).branchResult);
                    }
                    storeCache();
                }
                else if(inst instanceof Jp){
                    storeCache();
                }
                else if(inst instanceof IType){
                    if(((IType) inst).rs1 instanceof VirReg){
                        if(inCache((VirReg) ((IType) inst).rs1)){
                            updateCachePriority((VirReg) ((IType) inst).rs1);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((IType) inst).rs1,block));
                        ((IType) inst).rs1=VirRegMap.get(((IType) inst).rs1);
                    }
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
                else if(inst instanceof La){
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
                else if(inst instanceof Ld){
                    if(((Ld) inst).address instanceof VirReg){
                        if(inCache((VirReg) ((Ld) inst).address)){
                            updateCachePriority((VirReg) ((Ld) inst).address);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((Ld) inst).address,block));
                        ((Ld) inst).address=VirRegMap.get(((Ld) inst).address);
                    }
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
                else if(inst instanceof Li){
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
                else if(inst instanceof Lui){
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
                else if(inst instanceof Mv){
                    if(((Mv) inst).ori  instanceof VirReg){
                        if(inCache((VirReg) ((Mv) inst).ori)){
                            updateCachePriority((VirReg) ((Mv) inst).ori);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((Mv) inst).ori,block));
                        ((Mv) inst).ori=VirRegMap.get(((Mv) inst).ori);
                    }
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);

                    }
                }
                else if(inst instanceof RType){
                    if(((RType) inst).rs1 instanceof VirReg){
                        if(inCache((VirReg) ((RType) inst).rs1)){
                            updateCachePriority((VirReg) ((RType) inst).rs1);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((RType) inst).rs1,block));
                        ((RType) inst).rs1=VirRegMap.get(((RType) inst).rs1);
                    }
                    if(((RType) inst).rs2 instanceof VirReg){
                        if(inCache((VirReg) ((RType) inst).rs2)){
                            updateCachePriority((VirReg) ((RType) inst).rs2);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((RType) inst).rs2,block));
                        ((RType) inst).rs2=VirRegMap.get(((RType) inst).rs2);
                    }
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);

                    }
                }
                else if(inst instanceof St){
                    if(((St) inst).val instanceof VirReg){
                        if(inCache((VirReg) ((St) inst).val)){
                           updateCachePriority((VirReg) ((St) inst).val);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((St) inst).val,block));
                        ((St) inst).val=VirRegMap.get(((St) inst).val);
                    }
                    if(((St) inst).address instanceof VirReg){
                        if(inCache((VirReg) ((St) inst).address)){
                            updateCachePriority((VirReg) ((St) inst).address);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((St) inst).address,block));
                        ((St) inst).address=VirRegMap.get(((St) inst).address);
                    }
                }
                else if(inst instanceof Sz){
                    if(((Sz) inst).rs1 instanceof VirReg){
                        if(inCache((VirReg) ((Sz) inst).rs1)){
                            updateCachePriority((VirReg) ((Sz) inst).rs1);
                        }
                        else
                            block.addBefore(inst,loadVirReg((VirReg) ((Sz) inst).rs1,block));
                        ((Sz) inst).rs1=VirRegMap.get(((Sz) inst).rs1);
                    }
                    if(inst.dest instanceof VirReg){
                        if(inCache((VirReg) inst.dest)){
                            updateCachePriority((VirReg) inst.dest);
                        }
                        else
                            acquireCache((VirReg) inst.dest);
                        //block.addAfter(inst,storeVirReg((VirReg) inst.dest,block));
                        HaveModify.add((VirReg) inst.dest);
                        inst.dest=VirRegMap.get(inst.dest);
                    }
                }
            }
            //vipStoreCache();
        });
        func.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                inst.addStackSize(maxStack);
        });
    }
}
