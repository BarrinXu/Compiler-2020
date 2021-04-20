package Optimize;

import BackEnd.DomAnalysis;
import MIR.IRInst.*;
import MIR.Root;

public class DeadCodeElimination {
    public Root IRRoot;
    public boolean success;
    public DeadCodeElimination(Root IRRoot){
        this.IRRoot=IRRoot;
    }

    public void analysis(){
        boolean changed=true;
        while (changed){
            changed=false;

            for(var it:IRRoot.functions.entrySet()){
                var name=it.getKey();
                var func=it.getValue();
                for(var block:func.blocks){
                    for(var iter=block.phiInstMap.entrySet().iterator(); iter.hasNext();){
                        var entry=iter.next();
                        var register=entry.getKey();
                        var phiInst=entry.getValue();
                        if(register.usedInsts.size()==0){
                            iter.remove();
                            phiInst.remove(false);
                            changed=true;
                        }
                    }
                    for(var inst=block.head; inst!=null; inst=inst.nxt){
                        if(inst instanceof Binary||inst instanceof BitCast ||inst instanceof Cmp ||inst instanceof GetElementPtr||inst instanceof Load||inst instanceof Malloc||inst instanceof Zext){
                            //To be done->Call
                            if(inst.reg==null||inst.reg.usedInsts.size()==0){
                                inst.deleteInList();
                                inst.remove(false);
                                changed=true;
                            }
                        }
                    }
                    if(changed)
                        continue;
                    for(var iter=block.phiInstMap.entrySet().iterator(); iter.hasNext();){
                        var phiInst=iter.next();
                        var nowReg=phiInst.getKey();
                        boolean haveCircle=true;
                        while(true){
                            if(nowReg.usedInsts.size()!=1) {
                                haveCircle=false;
                                break;
                            }
                            if(nowReg.usedInsts.iterator().next() instanceof Phi && nowReg.usedInsts.iterator().next()!= phiInst.getValue()){
                                haveCircle=false;
                                break;
                            }
                            nowReg=nowReg.usedInsts.iterator().next().reg;
                            if(nowReg==null){
                                haveCircle=false;
                                break;
                            }
                            if(nowReg==phiInst.getKey())
                                break;
                        }
                        if(haveCircle){
                            changed=true;
                            nowReg= phiInst.getKey();
                            while(true){
                                var nxt=nowReg.usedInsts.iterator().next().reg;
                                if(nowReg.usedInsts.iterator().next() instanceof Phi){
                                    nowReg.usedInsts.iterator().next().remove(false);
                                    //iter.remove();
                                }
                                else
                                    nowReg.usedInsts.iterator().next().remove(true);
                                nowReg=nxt;
                                if(nowReg== phiInst.getKey())
                                    break;
                            }
                            iter.remove();
                        }
                    }
                }
                //new DomAnalysis(func).solve();
            }
            success|=changed;
        }
        IRRoot.functions.forEach((name,func)->new DomAnalysis(func).solve());
    }

    public boolean solve(){
        success=false;
        analysis();
        return success;
    }
}
