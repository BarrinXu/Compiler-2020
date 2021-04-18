package Optimize;

import MIR.Root;

public class Optimizer {
    public Root IRRoot;
    public Optimizer(Root IRRoot){
        this.IRRoot=IRRoot;
    }

    public void analysis(){
        boolean changed=true;
        while(changed){
            changed=new DeadCodeElimination(IRRoot).solve();
            changed|=new ConstantPropagation(IRRoot).solve();
        }
    }

    public void solve(){
        new FunctionInline(IRRoot,false).solve();
        analysis();
        while(new FunctionInline(IRRoot,true).solve())
            analysis();
        //new FunctionInline(IRRoot,true).solve();
        //analysis();
    }
}
