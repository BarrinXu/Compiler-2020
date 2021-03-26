package assembly;

import assembly.RISCVInst.BaseInst;

import java.util.ArrayList;

public class LIRBlock {
    public ArrayList<LIRBlock> fas=new ArrayList<>();
    public ArrayList<LIRBlock> sons=new ArrayList<>();
    public LIRBlock nxt=null;
    public boolean hasLst=false;

    public String name;
    public BaseInst head=null,tail=null;

    public LIRBlock(String name){
        this.name=name;
    }
    public void pushInst(BaseInst inst){
        if(head==null)
            head=tail=inst;
        else{
            tail.nxt=inst;
            inst.lst=tail;
            tail=inst;
        }
    }
    public void addBefore(BaseInst loc, BaseInst add){
        if(loc.lst==null)
            head=add;
        else
            loc.lst.nxt=add;
        add.lst= loc.lst;
        add.nxt= loc;
        loc.lst=add;
    }
    public void addAfter(BaseInst loc, BaseInst add){
        if(loc.nxt==null)
            tail=add;
        else
            loc.nxt.lst=add;
        add.lst=loc;
        add.nxt=loc.nxt;
        loc.nxt=add;
    }
    @Override
    public String toString(){
        return name;
    }
}
