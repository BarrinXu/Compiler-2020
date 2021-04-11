package Optimize;

import MIR.Root;

public class DCE {
    public Root IRRoot;
    boolean success;
    public DCE(Root IRRoot){
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
                        var phiInst=iter.next();
                        var nowReg=phiInst.getKey();
                        boolean haveCircle=true;
                        while(true){
                            if(nowReg.usedInsts.size()!=1) {
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
                                nowReg.usedInsts.iterator().next().remove();
                                nowReg=nxt;
                                if(nowReg== phiInst.getKey())
                                    break;
                            }
                            //iter.remove();
                        }
                    }
                }
            }
            success|=changed;
        }
    }

    public boolean solve(){
        success=false;
        analysis();
        return success;
    }
}
