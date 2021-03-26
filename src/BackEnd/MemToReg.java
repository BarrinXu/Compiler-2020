package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Load;
import MIR.IRInst.Phi;
import MIR.IRInst.Store;
import MIR.IROperand.IRBaseOperand;
import MIR.IROperand.Register;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MemToReg {
    public Root IRRoot;
    public MemToReg(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public void solve(){
        IRRoot.functions.forEach((name,func)->{
            solveFunc(func);
        });
    }

    public IRBaseOperand solveReplace(HashMap<Register,IRBaseOperand>replaceMap,IRBaseOperand updVar){
        IRBaseOperand updTo=updVar;
        while(replaceMap.containsKey(updTo))
            updTo=replaceMap.get(updTo);
        return updTo;
    }

    public void solveFunc(Function func){
        HashSet<IRBlock> haveDefBlocks=new HashSet<>();
        HashMap<IRBlock, HashSet<Load>> allocLoads=new HashMap<>();
        HashMap<IRBlock,HashMap<Register, IRBaseOperand>>allocStores=new HashMap<>();
        HashMap<IRBlock,HashMap<Register, Phi>>allocPhis=new HashMap<>();
        HashMap<Register,IRBaseOperand>replaceMap=new HashMap<>();

        new DomAnalysis(func).solve();

        func.blocks.forEach(block -> {
            allocPhis.put(block,new HashMap<>());
            allocLoads.put(block,new HashSet<>());
            allocStores.put(block,new HashMap<>());
        });
        for(var block:func.blocks) {
            for(var inst=block.head; inst!=null;){
                var nxtInst=inst.nxt;
                if(inst instanceof Load){
                    var address=((Load) inst).address;
                    if(address instanceof Register&&func.registers.contains(address)){
                        if(allocStores.get(inst.block).containsKey(address)){
                            replaceMap.put(inst.reg,allocStores.get(inst.block).get(address));
                            inst.remove();
                        }
                        else
                            allocLoads.get(inst.block).add((Load) inst);
                    }
                }
                else if(inst instanceof Store){
                    IRBaseOperand address=((Store) inst).address;
                    if(address instanceof Register&&func.registers.contains(address)){
                        haveDefBlocks.add(inst.block);
                        allocStores.get(inst.block).put((Register) address,((Store) inst).val);
                        inst.remove();
                    }
                }
                inst=nxtInst;
            }
        }
        while(!haveDefBlocks.isEmpty()){
            var nowDealBlocks=haveDefBlocks;
            haveDefBlocks=new HashSet<>();
            for(var nowDealBlock:nowDealBlocks){
                if(!allocStores.get(nowDealBlock).isEmpty()){
                    for(IRBlock domBlock :nowDealBlock.domBlocks){
                        for(var entry:allocStores.get(nowDealBlock).entrySet()){
                            var allocVar=entry.getKey();
                            var val=entry.getValue();
                            if(!allocPhis.get(domBlock).containsKey(allocVar)){
                                var dest=new Register(val.type, allocVar.name+"_ToPhi");
                                var phiInst=new Phi(dest, domBlock,new ArrayList<>(),new ArrayList<>());
                                domBlock.add_PhiInst(phiInst);
                                if(!allocStores.get(domBlock).containsKey(allocVar)){
                                    allocStores.get(domBlock).put(allocVar,dest);
                                    haveDefBlocks.add(domBlock);
                                }
                                allocPhis.get(domBlock).put(allocVar,phiInst);
                            }
                        }
                    }
                }
            }
        }
        func.blocks.forEach(block -> {
            if(!allocPhis.get(block).isEmpty()){
                allocPhis.get(block).forEach((address,phiInst)->{
                    block.fas.forEach(fa->{
                        IRBlock nowBlock=fa;
                        while(!allocStores.get(nowBlock).containsKey(address))
                            nowBlock=nowBlock.imDom;
                        phiInst.blocks.add(fa);
                        phiInst.operands.add(allocStores.get(nowBlock).get(address));
                        allocStores.get(nowBlock).get(address).addUsedInst(phiInst);
                    });
                });
            }
            if(!allocLoads.get(block).isEmpty()){
                allocLoads.get(block).forEach(loadInst->{
                    assert loadInst.address instanceof Register;
                    var updVar=loadInst.address;
                    IRBaseOperand updTo;
                    if(allocPhis.get(block).containsKey(updVar))
                        updTo=allocPhis.get(block).get(updVar).reg;
                    else{
                        IRBlock nowBlock=block.imDom;
                        while(!allocStores.get(nowBlock).containsKey(updVar))
                            nowBlock=nowBlock.imDom;
                        updTo=allocStores.get(nowBlock).get(updVar);
                    }
                    replaceMap.put(loadInst.reg, solveReplace(replaceMap,updTo));
                    loadInst.remove();
                });
            }
        });
        replaceMap.forEach((oriReg,updTp)->{
            oriReg.replaceUse(solveReplace(replaceMap,updTp));
        });
    }
}
