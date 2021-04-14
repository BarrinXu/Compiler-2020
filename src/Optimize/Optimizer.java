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
        }
    }

    public void solve(){
        analysis();
    }
}
