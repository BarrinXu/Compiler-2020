package assembly.RISCVInst;

import assembly.LIRBlock;
import assembly.LOperand.Reg;

import java.util.Collections;
import java.util.HashSet;

public abstract class BaseInst {
    public Reg dest;
    public LIRBlock LBlock;

    public BaseInst lst=null,nxt=null;

    public enum CalOpType{
        add,sub,sll,slt,sltu,xor,srl,sra,or,and,mul,div,rem
    }
    public enum CmpOpType{
        beq,bne
    }

    public BaseInst(Reg dest,LIRBlock LBlock){
        this.dest=dest;
        this.LBlock=LBlock;
    }
    public abstract String toString();
    public abstract void addStackSize(int stackSize);
    public abstract HashSet<Reg> usedRegSet();
    public HashSet<Reg> defRegSet(){
        //Maybe
        if(dest!=null)
            return new HashSet<>(Collections.singleton(dest));
        else if(this instanceof Ca)
            return new HashSet<>(((Ca) this).root.callerRegs);
        else
            return new HashSet<>();
    }
    public abstract void replaceUse(Reg origin, Reg to);
    public void replaceDest(Reg origin, Reg to){
        if(dest==origin)
            dest=to;
    }
    public void addBefore(BaseInst inst){
        inst.lst=lst;
        inst.nxt=this;
        if(lst!=null)
            lst.nxt=inst;
        else
            LBlock.head=inst;
        lst=inst;
    }
    public void addAfter(BaseInst inst){
        inst.lst=this;
        inst.nxt=nxt;
        if(nxt!=null)
            nxt.lst=inst;
        else
            LBlock.tail=inst;
        nxt=inst;
    }
    public void replaceSelf(BaseInst updInst){
        updInst.lst=lst;
        updInst.nxt=nxt;
        if(lst!=null)
            lst.nxt=updInst;
        else
            LBlock.head=updInst;
        if(nxt!=null)
            nxt.lst=updInst;
        else
            LBlock.tail=updInst;
    }
    public void deleteSelf(){
        if(lst!=null)
            lst.nxt=nxt;
        else
            LBlock.head=nxt;
        if(nxt!=null)
            nxt.lst=lst;
        else
            LBlock.tail=lst;
    }
}
