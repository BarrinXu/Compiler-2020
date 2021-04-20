package Optimize;

import MIR.IRBlock;
import MIR.IRInst.*;
import MIR.IROperand.Parameter;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashSet;

public class CommonSubexpressions {
    public Root IRRoot;
    public boolean success;
    public CommonSubexpressions(Root IRRoot){
        this.IRRoot=IRRoot;
    }
    public boolean solve(){
        success=false;
        IRRoot.functions.forEach((name,func)-> {
            func.blocks.forEach(this::solveBlock);
        });
        return success;
    }
    public void solveBlock(IRBlock block){
        boolean changed=true;
        while(changed){
            changed=false;
            ArrayList<Inst>visitInsts=new ArrayList<>();
            for(var inst=block.head; inst!=null; inst=inst.nxt){
                if(!(inst instanceof Branch||inst instanceof Jump||inst instanceof Call||inst instanceof Load ||inst instanceof Malloc||inst instanceof Phi||inst instanceof Return||inst instanceof Store)){
                    boolean flag=false;
                    for(var vip:visitInsts){
                        if(vip.same(inst)){
                            flag=true;
                            inst.reg.replaceUse(vip.reg);
                            inst.deleteInList();
                            inst.remove(false);
                            break;
                        }
                    }
                    if(!flag)
                        visitInsts.add(inst);
                    else
                        changed=true;
                }
            }
            HashSet<Phi>visitPhis=new HashSet<>();
            for(var iter=block.phiInstMap.entrySet().iterator(); iter.hasNext(); ){
                var phiInst=iter.next().getValue();
                boolean flag=false;
                for(var vip:visitPhis){
                    if(vip.same(phiInst)){
                        flag=true;
                        phiInst.reg.replaceUse(vip.reg);
                        break;
                    }
                }
                if(!flag)
                    visitPhis.add(phiInst);
                else{
                    iter.remove();
                    phiInst.remove(false);
                    changed=true;
                }
            }
            success|=changed;
        }
    }
}
