package MIR;

import MIR.IRInst.*;
import MIR.IROperand.Register;
import com.sun.jdi.InternalException;

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
    public int loopDep;
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
        tmp.remove(true);
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
    public void modifyBranchDestOfDirectJumpCompress(IRBlock ori, IRBlock to){
        if(tail instanceof Jump){
            if(((Jump) tail).destBlock==ori){
                ((Jump) tail).destBlock=to;
                sons.remove(ori);
                sons.add(to);
                to.fas.add(this);
            }
        }
        else if(tail instanceof Branch)
        {
            if(((Branch) tail).thenDestBlock==ori){
                ((Branch) tail).thenDestBlock=to;
                sons.remove(ori);
                sons.add(to);
                to.fas.add(this);
            }
            if(((Branch) tail).elseDestBlock==ori){
                ((Branch) tail).elseDestBlock=to;
                sons.remove(ori);
                sons.add(to);
                to.fas.add(this);
            }
        }
    }
    public void modifyPhi(IRBlock ori, IRBlock to){
        phiInstMap.forEach((register, phiInst) -> {
            int size=phiInst.blocks.size();
            for(int i=0; i<size; i++)
                if(phiInst.blocks.get(i)==ori)
                    phiInst.blocks.set(i,to);
        });
    }
    public void split(IRBlock latterBlock,Inst inst){
        sons.forEach(son->{
            son.modifyPhi(this,latterBlock);
            son.fas.remove(this);
            son.fas.add(latterBlock);
        });
        latterBlock.sons.addAll(sons);
        sons.clear();

        inst.nxt.lst=null;
        latterBlock.head=inst.nxt;
        latterBlock.tail=tail;
        tail=inst.lst;
        for(var instLatter=inst.nxt; instLatter!=null; instLatter=instLatter.nxt)
            instLatter.block=latterBlock;
        terminate=false;
        latterBlock.terminate=true;
        if(tail!=null)
            tail.nxt=null;
        if(head==inst)
            head=null;
    }
    public void mergeBlock(IRBlock rhs){
        if(rhs.fas.size()!=0)
            throw new InternalException();
        sons.addAll(rhs.sons);
        rhs.sons.forEach(son->{
            if(son.fas.contains(rhs)){
                son.fas.remove(rhs);
                son.fas.add(this);
                son.modifyPhi(rhs,this);
            }
        });
        rhs.phiInstMap.forEach(((register, phiInst) -> phiInst.block=this));
        for(var inst=rhs.head; inst!=null; inst=inst.nxt)
            inst.block=this;
        if(tail!=null)
            tail.nxt=rhs.head;
        if(rhs.head!=null)
            rhs.head.lst=tail;
        if(head==null)
            head=rhs.head;
        tail=rhs.tail;
        terminate= rhs.terminate;
    }
    public void modifyFa(IRBlock ori, IRBlock to){
        if(fas.contains(ori)){
            fas.remove(ori);
            fas.add(to);
            modifyPhi(ori,to);
        }
    }
}
