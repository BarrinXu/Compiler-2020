package BackEnd;

import assembly.LFunction;
import assembly.LIRBlock;
import assembly.LOperand.LGlobalReg;
import assembly.LRoot;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class PrintAsm {
    public LRoot root;
    PrintStream stream;
    public ArrayList<LIRBlock> blocks=new ArrayList<>();
    public PrintAsm(LRoot root, PrintStream stream){
        this.root=root;
        this.stream=stream;
    }
    public void solve(){
        stream.println("\t.text");
        root.functions.forEach(this::solveFunc);
        root.globalRegs.forEach(this::solveGlobalReg);
        root.strings.forEach(this::solveString);
    }

    public void solveFunc(LFunction func){
        blocks.clear();
        collect(func);
        stream.println("\t.globl\t"+func.name);
        stream.println("\t.p2align\t1");
        stream.println("\t.type\t"+func.name+",@function");
        stream.println(func.name+":");
        solveBlock(func.inBlock);
        blocks.forEach(block -> {
            if(!alreadyVisit.contains(block)&&!block.hasLst)
                solveBlock(block);
        });
        stream.println("\t.size\t"+func.name+", "+".-"+func.name+"\n");
    }
    public HashSet<LIRBlock> alreadyVisit=new HashSet<>();
    public void solveBlock(LIRBlock block){
        assert !alreadyVisit.contains(block);
        alreadyVisit.add(block);
        stream.println(block.toString()+": ");
        for(var inst=block.head; inst!=null; inst=inst.nxt){
            stream.println("\t"+inst.toString());
        }
        if(block.nxt!=null)
            solveBlock(block.nxt);
    }
    public void collect(LFunction func){
        int cnt=0;
        Queue<LIRBlock> queue=new LinkedList<>();
        blocks.add(func.inBlock);
        queue.add(func.inBlock);
        while(!queue.isEmpty()){
            var head=queue.poll();
            head.name="."+func.name+"_b."+cnt++;
            head.sons.forEach(son->{
                if(son!=null&&!blocks.contains(son)){
                    blocks.add(son);
                    queue.add(son);
                }
            });
        }
    }
    public void solveGlobalReg(LGlobalReg reg){
        stream.println("\t.type\t"+reg.name+",@object");
        stream.println("\t.section\t.bss");
        stream.println("\t.globl\t"+reg.name);
        stream.println("\t.p2align\t2");
        stream.println(reg.name+":");
        stream.println(".L"+reg.name+"$local:");
        stream.println("\t.word\t0");
        stream.println("\t.size\t"+reg.name+", 4\n");
    }
    public void solveString(LGlobalReg reg,String string){
        stream.println("\t.type\t"+reg.name+",@object");
        stream.println("\t.section\t.rodata");
        stream.println(reg.name+":");
        String transString;
        transString=string.replace("\\","\\\\");
        transString=transString.replace("\n","\\n");
        transString=transString.replace("\0","");
        transString=transString.replace("\t","\\t");
        transString=transString.replace("\"","\\\"");
        stream.println("\t.asciz\t\"" + transString + "\"");
        stream.println("\t.size\t" + reg.name + ", " + string.length() + "\n");
    }
}
