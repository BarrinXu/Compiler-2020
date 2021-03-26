package MIR;

import MIR.IRInst.*;
import MIR.IROperand.Register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IRBlock {
    public ArrayList<IRBlock>fas=new ArrayList<>();
    public ArrayList<IRBlock>sons=new ArrayList<>();
    public HashMap<Register,Phi>phiInstMap=new HashMap<>();
    public IRBlock imDom=null;
    public HashSet<IRBlock> domBlocks=new HashSet<>();
    public Inst head=null, tail=null;
    public String name;

    public boolean terminate;

    public IRBlock(String name){
        this.name=name;
    }

    public void add_PhiInst(Phi inst){
        phiInstMap.put(inst.reg,inst);
    }

    public void pushInst(Inst inst){
        if(head==null)
            head=tail=inst;
        else{
            tail.nxt=inst;
            inst.lst=tail;
            tail=inst;
        }
    }

    public void pushHeadInst(Inst inst){
        if(head==null)
            head=tail=inst;
        else{
            inst.nxt=head;
            head.lst=inst;
            head=inst;
        }
    }
    public void addInstBack(Inst inst){
        Inst pre_tail=tail.lst;
        tail.lst=inst;
        if(pre_tail==null){
            inst.lst=null;
            inst.nxt=tail;
            head=inst;
        }
        else{
            pre_tail.nxt=inst;
            inst.lst=pre_tail;
            inst.nxt=tail;
        }
        inst.block=this;
    }
    public void setTerminate(Inst inst){
        pushInst(inst);
        terminate=true;
        if(inst instanceof Jump){
            var destBlock=((Jump) inst).destBlock;
            sons.add(destBlock);
            destBlock.fas.add(this);
        }
        else if(inst instanceof Branch){
            var destBlock=((Branch) inst).thenDestBlock;
            sons.add(destBlock);
            destBlock.fas.add(this);

            destBlock=((Branch) inst).elseDestBlock;
            sons.add(destBlock);
            destBlock.fas.add(this);
        }
    }

    public void deleteTerminate(){
        if(!terminate)
            return;
        terminate=false;
        var tmp=tail;
        if(tmp instanceof Jump)
            deleteSon(((Jump) tmp).destBlock);
        else if(tmp instanceof Branch){
            deleteSon(((Branch) tmp).thenDestBlock);
            deleteSon(((Branch) tmp).elseDestBlock);
        }
        tmp.deleteInList();
        tmp.remove();
    }

    public void deleteSon(IRBlock son){
        son.deleteFa(this);
        sons.remove(son);
    }

    public void deleteFa(IRBlock fa){
        fas.remove(fa);
        phiInstMap.forEach((reg,phi)->phi.deleteBlock(fa));
    }

    public void modifySon(IRBlock oriSon,IRBlock newSon){
        if(tail instanceof Jump){
            deleteTerminate();
            setTerminate(new Jump(this,newSon));
        }
        else{
            assert terminate;
            assert tail instanceof Branch;
            Branch oldTerminate= (Branch) tail,newTerminate;
            if(oldTerminate.thenDestBlock==oriSon)
                newTerminate=new Branch(this, oldTerminate.condition, newSon, oldTerminate.elseDestBlock);
            else
                newTerminate=new Branch(this, oldTerminate.condition, oldTerminate.thenDestBlock, newSon);
            deleteTerminate();
            setTerminate(newTerminate);
        }
    }
    public void removeInst(Inst inst){
        if(inst instanceof Phi)
            phiInstMap.remove(inst.reg);
        else if(inst instanceof Branch||inst instanceof Jump||inst instanceof Return)
            deleteTerminate();
        else
            inst.deleteInList();
    }
    public boolean terminateWithReturn(){
        return terminate && tail instanceof Return;
    }

}
