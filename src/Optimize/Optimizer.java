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
            changed=new SpecialForInlineAdv(IRRoot).solve();
            changed|=new DeadCodeElimination(IRRoot).solve();
            changed|=new ConstantPropagation(IRRoot).solve();

        }
    }

    public void solve(){
        //new FunctionInline(IRRoot,false).solve();
        analysis();
        var vip=new FunctionInline(IRRoot,true);
        //vip.solve();
        int cnt=0;
        while(cnt<9&&vip.solve()){
            cnt++;
            analysis();
        }
        //new FunctionInline(IRRoot,true).solve();
        //analysis();
    }
}
