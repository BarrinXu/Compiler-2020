package BackEnd;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.RealReg;
import assembly.LOperand.Reg;
import assembly.LOperand.StackImm;
import assembly.LOperand.VirReg;
import assembly.LRoot;
import assembly.RISCVInst.Ld;
import assembly.RISCVInst.Mv;
import assembly.RISCVInst.St;

import java.util.*;

public class UltimateRegAlloc {
    public LRoot lRoot;

    public int K;
    public static int inf=1000000000;
    public int stackSize=0;
    public LFunction nowFunc;

    public Stack<Reg> selectStack=new Stack<>();
    public HashSet<Reg> preColored;

    public HashSet<Reg> initial=new LinkedHashSet<>();
    public HashSet<Reg> spillWorkList=new LinkedHashSet<>();
    public HashSet<Reg> freezeWorkList=new HashSet<>();
    public HashSet<Reg> simplifyWorkList=new LinkedHashSet<>();
    public HashSet<Reg> spilledNodes=new LinkedHashSet<>();
    public HashSet<Reg> coloredNodes=new HashSet<>();
    public HashSet<Reg> coalescedNodes=new LinkedHashSet<>();
    public HashSet<Reg> spillIntroduce=new HashSet<>();

    public HashSet<Mv>workListMoves=new LinkedHashSet<>();
    public HashSet<Mv>activeMoves=new HashSet<>();
    public HashSet<Mv>coalescedMoves=new HashSet<>();
    public HashSet<Mv>constrainedMoves=new HashSet<>();
    public HashSet<Mv>frozenMoves=new HashSet<>();

    public static class Edge{
        Reg u,v;
        public Edge(Reg u,Reg v){
            this.u=u;
            this.v=v;
        }
        @Override
        public boolean equals(Object rhs){
            return rhs instanceof Edge && (((Edge) rhs).u==u&&((Edge) rhs).v==v);
        }
        @Override
        public int hashCode(){
            return u.toString().hashCode()^v.toString().hashCode();
        }
    }
    public HashSet<Edge>edgeSet=new HashSet<>();
    public void addEdge(Reg u,Reg v){
        if(u!=v&&!edgeSet.contains(new Edge(u,v))){
            edgeSet.add(new Edge(u,v));
            edgeSet.add(new Edge(v,u));
            if(!preColored.contains(u)){
                u.linkSet.add(v);
                u.deg++;
            }
            if(!preColored.contains(v)){
                v.linkSet.add(u);
                v.deg++;
            }
        }
    }

    public class LiveAnalysis{
        public LFunction func;
        public HashSet<LIRBlock>visit=new HashSet<>();
        public HashMap<LIRBlock, HashSet<Reg>>blockUseSet=new HashMap<>();
        public HashMap<LIRBlock, HashSet<Reg>>blockDefSet=new HashMap<>();
        public LiveAnalysis(LFunction func){
            this.func=func;
        }
        public void solve(){
            func.blocks.forEach(this::solveBlock);
            liveInfoDFS(func.outBlock);
        }
        public void solveBlock(LIRBlock block){
            HashSet<Reg> useSet=new HashSet<>();
            HashSet<Reg> defSet=new HashSet<>();
            for(var inst=block.head; inst!=null; inst=inst.nxt){
                HashSet<Reg>nowUseSet=inst.usedRegSet();
                nowUseSet.removeAll(defSet);
                useSet.addAll(nowUseSet);
                defSet.addAll(inst.defRegSet());
            }
            blockUseSet.put(block,useSet);
            blockDefSet.put(block,defSet);
            block.liveIn.clear();
            block.liveOut.clear();
        }
        public void liveInfoDFS(LIRBlock block){
            visit.add(block);
            HashSet<Reg>liveOut=new HashSet<>();
            block.sons.forEach(son->{
                liveOut.addAll(son.liveIn);
            });
            HashSet<Reg>liveIn=new HashSet<>(liveOut);
            liveIn.removeAll(blockDefSet.get(block));
            liveIn.addAll(blockUseSet.get(block));
            block.liveOut.addAll(liveOut);
            liveIn.removeAll(block.liveIn);
            if(!liveIn.isEmpty()){
                block.liveIn.addAll(liveIn);
                visit.removeAll(block.fas);
            }
            block.fas.forEach(fa->{
                if(!visit.contains(fa))
                    liveInfoDFS(fa);
            });
        }
    }

    public UltimateRegAlloc(LRoot lRoot){
        this.lRoot=lRoot;
        K=lRoot.assignRegs.size();
        preColored=new HashSet<>(lRoot.realRegs);
    }

    public void getSpillPriority(LFunction func){
        func.blocks.forEach(block -> {
            double weight=1;
            for(var inst=block.head; inst!=null; inst=inst.nxt){
                inst.usedRegSet().forEach(reg -> {
                    reg.weight+=weight;
                });
                if(inst.dest!=null)
                    inst.dest.weight+=weight;
            }
        });
    }

    public void init(){
        initial.clear();
        simplifyWorkList.clear();
        freezeWorkList.clear();
        spillWorkList.clear();
        spilledNodes.clear();
        coalescedNodes.clear();
        coloredNodes.clear();
        selectStack.clear();

        coalescedMoves.clear();
        constrainedMoves.clear();
        frozenMoves.clear();
        activeMoves.clear();
        workListMoves.clear();

        edgeSet.clear();
        nowFunc.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt){
                initial.addAll(inst.defRegSet());
                initial.addAll(inst.usedRegSet());
            }
        });
        initial.removeAll(preColored);
        initial.forEach(reg -> {
            reg.deg=0;
            reg.alias=null;
            reg.color=null;
            reg.weight=0;
            reg.linkSet.clear();
            reg.moveSet.clear();
        });
        preColored.forEach(reg -> {
            reg.deg=inf;
            reg.alias=null;
            reg.color= (RealReg) reg;
            reg.linkSet.clear();
            reg.moveSet.clear();
        });
        getSpillPriority(nowFunc);
    }

    public void solve(){
        lRoot.functions.forEach(func->{
            nowFunc=func;
            stackSize=0;
            solveFunc(func);
            stackSize+= func.parametersOffset;
            if(stackSize%16!=0)
                stackSize+=16-stackSize%16;
            finalUpd();

            //To be done
        });
    }
    public void solveFunc(LFunction func){
        boolean finish=false;
        while(!finish){
            init();
            new LiveAnalysis(func).solve();
            build();
            initial.forEach(node->{
                if(node.deg>=K)
                    spillWorkList.add(node);
                else if(isMoveRelated(node))
                    freezeWorkList.add(node);
                else
                    simplifyWorkList.add(node);
            });
            do{
                if(!simplifyWorkList.isEmpty())
                    simplify();
                else if(!workListMoves.isEmpty())
                    coalesce();
                else if(!freezeWorkList.isEmpty())
                    freeze();
                else if(!spillWorkList.isEmpty())
                    selectSpill();
            }
            while(!(simplifyWorkList.isEmpty()&&workListMoves.isEmpty()&&freezeWorkList.isEmpty()&&spillWorkList.isEmpty()));
            assignColors();
            if(!spilledNodes.isEmpty()){
                rewriteProgram();
                finish=false;
            }
            else
                finish=true;
        }
    }
    public void build(){
        nowFunc.blocks.forEach(block -> {
            HashSet<Reg>nowLiveRegSet=new HashSet<>(block.liveOut);
            for(var inst=block.tail; inst!=null; inst=inst.lst){
                if(inst instanceof Mv){
                    nowLiveRegSet.removeAll(inst.usedRegSet());
                    HashSet<Reg> mvRegSet =inst.usedRegSet();
                    mvRegSet.addAll(inst.defRegSet());
                    for(var reg:mvRegSet)
                        reg.moveSet.add((Mv) inst);
                    workListMoves.add((Mv) inst);
                }
                var defRegSet =inst.defRegSet();
                nowLiveRegSet.add(lRoot.realRegs.get(0));
                nowLiveRegSet.addAll(defRegSet);
                defRegSet.forEach(defReg->{
                    nowLiveRegSet.forEach(liveReg->{
                        addEdge(liveReg,defReg);
                    });
                });
                nowLiveRegSet.removeAll(defRegSet);
                nowLiveRegSet.addAll(inst.usedRegSet());
            }
        });
    }
    public HashSet<Reg> regLinkSet(Reg center){
        HashSet<Reg> tmp=new HashSet<>(center.linkSet);
        tmp.removeAll(selectStack);
        tmp.removeAll(coalescedNodes);
        return tmp;
    }
    public HashSet<Reg> twoRegsLinkSet(Reg u, Reg v){
        HashSet<Reg> tmp=new HashSet<>(regLinkSet(u));
        tmp.addAll(regLinkSet(v));
        return tmp;
    }
    public HashSet<Mv> nodeMvSet(Reg x){
        HashSet<Mv>tmp=new HashSet<>(workListMoves);
        tmp.addAll(activeMoves);
        tmp.retainAll(x.moveSet);
        return tmp;
    }
    public void enableMoves(HashSet<Reg>nodeSet){
        nodeSet.forEach(node->{
            nodeMvSet(node).forEach(mv -> {
                if(activeMoves.contains(mv)){
                    activeMoves.remove(mv);
                    workListMoves.add(mv);
                }
            });
        });
    }
    public boolean isMoveRelated(Reg x){
        return !nodeMvSet(x).isEmpty();
    }
    public void decrementDegree(Reg x){
        x.deg--;
        if(x.deg==K-1){
            HashSet<Reg>nodes=new HashSet<>(regLinkSet(x));
            nodes.add(x);
            enableMoves(nodes);
            spillWorkList.remove(x);
            if(isMoveRelated(x))
                freezeWorkList.add(x);
            else
                simplifyWorkList.add(x);
        }
    }
    public void simplify(){
        var node=simplifyWorkList.iterator().next();
        simplifyWorkList.remove(node);
        selectStack.push(node);
        regLinkSet(node).forEach(this::decrementDegree);
    }
    public void addWorkList(Reg node){
        if((!preColored.contains(node))&&(!isMoveRelated(node))&&node.deg<K){
            freezeWorkList.remove(node);
            simplifyWorkList.add(node);
        }
    }
    public boolean Briggs(HashSet<Reg> nodes){
        int cnt=0;
        for(var node:nodes) {
            if(node.deg >= K)
                cnt++;
        }
        return cnt<K;
    }
    public boolean George(Reg u,Reg v){
        boolean tmp=true;
        for(var x:regLinkSet(v))
            tmp&=(x.deg<K||preColored.contains(x)||edgeSet.contains(new Edge(x,u)));
        return tmp;
    }
    public void combine(Reg u,Reg v){
        if(freezeWorkList.contains(v))
            freezeWorkList.remove(v);
        else
            spillWorkList.remove(v);
        coalescedNodes.add(v);
        v.alias=u;
        u.moveSet.addAll(v.moveSet);
        HashSet<Reg>tmp=new HashSet<>();
        tmp.add(v);
        enableMoves(tmp);
        regLinkSet(v).forEach(x->{
            addEdge(x,u);
            decrementDegree(x);//Maybe change!
        });
        if(u.deg>=K&&freezeWorkList.contains(u)){
            freezeWorkList.remove(u);
            spillWorkList.add(u);
        }
    }
    public void coalesce(){
        //func title maybe change!
        var mv=workListMoves.iterator().next();
        var x=getAlias(mv.dest);
        var y=getAlias(mv.ori);
        Reg u,v;
        if(preColored.contains(y)){
            u=y;
            v=x;
        }
        else{
            u=x;
            v=y;
        }
        workListMoves.remove(mv);
        if(u==v){
            coalescedMoves.add(mv);
            addWorkList(u);
        }
        else if(preColored.contains(v)||edgeSet.contains(new Edge(u,v))){
            constrainedMoves.add(mv);
            addWorkList(u);
            addWorkList(v);
        }
        else{
            if((preColored.contains(u)&& George(u,v))||((!preColored.contains(u))&&Briggs(twoRegsLinkSet(u,v)))){
                coalescedMoves.add(mv);
                combine(u,v);
                addWorkList(u);
            }
            else
                activeMoves.add(mv);
        }
    }
    public void freeze(){
        var x=freezeWorkList.iterator().next();
        freezeWorkList.remove(x);
        simplifyWorkList.add(x);
        freezeMoves(x);
    }
    public void selectSpill(){
        Reg x=getSpillNode();
        spillWorkList.remove(x);
        simplifyWorkList.add(x);
        freezeMoves(x);
    }
    public void assignColors(){
        while(!selectStack.isEmpty()){
            Reg x=selectStack.pop();
            ArrayList<RealReg>availableColors=new ArrayList<>(lRoot.assignRegs);
            HashSet<Reg>colored=new HashSet<>(coloredNodes);
            colored.addAll(preColored);
            x.linkSet.forEach(rx->{
                if(colored.contains(getAlias(rx)))
                    availableColors.remove(getAlias(rx).color);
            });
            if(availableColors.isEmpty())
                spilledNodes.add(x);
            else{
                x.color=availableColors.get(0);
                coloredNodes.add(x);
            }
        }
        coalescedNodes.forEach(node->node.color=getAlias(node).color);
    }
    public void rewriteProgram(){
        spilledNodes.forEach(node->{
            node.stackOffset=new StackImm(-1*stackSize-4);
            stackSize+=4;
        });
        nowFunc.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                if(inst.dest!=null&&inst.dest instanceof VirReg)
                    getAlias(inst.dest);
        });
        nowFunc.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt) {
                for (var reg : inst.usedRegSet())
                    if (reg.stackOffset != null) {
                        if (inst.defRegSet().contains(reg)) {
                            VirReg tmp = new VirReg(++nowFunc.regCnt, ((VirReg) reg).size);
                            spillIntroduce.add(tmp);
                            inst.replaceUse(reg, tmp);
                            inst.replaceDest(reg, tmp);
                            inst.addBefore(new Ld(tmp, block, lRoot.realRegs.get(2), reg.stackOffset, tmp.size));
                            inst.addAfter(new St(block, lRoot.realRegs.get(2), reg.stackOffset, tmp, tmp.size));
                        } else {
                            if (inst instanceof Mv && ((Mv) inst).ori == reg && inst.dest.stackOffset == null) {
                                var replacedInst = new Ld(inst.dest, block, lRoot.realRegs.get(2), reg.stackOffset, ((VirReg) reg).size);
                                inst.replaceSelf(replacedInst);
                                inst = replacedInst;
                            } else {
                                VirReg tmp = new VirReg(++nowFunc.regCnt, ((VirReg) reg).size);
                                spillIntroduce.add(tmp);
                                inst.addBefore(new Ld(tmp, block, lRoot.realRegs.get(2), reg.stackOffset, tmp.size));
                                inst.replaceUse(reg, tmp);
                            }
                        }
                    }
                for(var reg:inst.defRegSet())
                    if(reg.stackOffset!=null){
                        if(!inst.usedRegSet().contains(reg)){
                            if(inst instanceof Mv &&((Mv) inst).ori.stackOffset==null){
                                var replacedInst=new St(block,lRoot.realRegs.get(2),reg.stackOffset,((Mv) inst).ori,((VirReg)reg).size);
                                inst.replaceSelf(replacedInst);
                                inst=replacedInst;
                            }
                            else{
                                var tmp=new VirReg(++nowFunc.regCnt,((VirReg)reg).size);
                                spillIntroduce.add(tmp);
                                inst.replaceDest(reg,tmp);
                                inst.addAfter(new St(block,lRoot.realRegs.get(2),reg.stackOffset,tmp,((VirReg) reg).size));
                            }
                        }
                    }
            }
        });
    }
    public void finalUpd(){
        nowFunc.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                inst.addStackSize(stackSize);
        });
        nowFunc.blocks.forEach(block -> {
            for(var inst=block.head; inst!=null; inst=inst.nxt)
                if(inst instanceof Mv&&((Mv) inst).ori.color==inst.dest.color)
                    inst.deleteSelf();
        });
        //To be done-optimize block mix
    }
    public Reg getSpillNode(){
        Reg minn=null;
        double minv=0;
        var it=spillWorkList.iterator();
        while(it.hasNext()){
            minn=it.next();
            minv=minn.weight/minn.deg;
            if(!spillIntroduce.contains(minn))
                break;
        }//why
        while(it.hasNext()){
            var reg=it.next();
            if((!spillIntroduce.contains(reg))&&reg.weight/reg.deg<minv){
                minn=reg;
                minv=reg.weight/reg.deg;
            }
        }
        return minn;
    }
    public void freezeMoves(Reg u){
        nodeMvSet(u).forEach(mv -> {
            var x=mv.dest;
            var y=mv.ori;
            Reg v;
            if(getAlias(u)==getAlias(y))
                v=getAlias(x);
            else
                v=getAlias(y);
            activeMoves.remove(mv);
            frozenMoves.add(mv);
            if(v.deg<K&&nodeMvSet(v).isEmpty()){
                freezeWorkList.remove(v);
                simplifyWorkList.add(v);
            }
        });
    }
    public Reg getAlias(Reg x){
        if(coalescedNodes.contains(x)){
            var alias=getAlias(x.alias);
            x.alias=alias;
            return alias;
            //Maybe
        }
        else
            return x;
    }
}
