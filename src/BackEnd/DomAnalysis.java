package BackEnd;

import MIR.Function;
import MIR.IRBlock;

import java.util.ArrayList;
import java.util.HashMap;

public class DomAnalysis {
    public Function func;

    ArrayList<ArrayList<IRBlock>> bucket=new ArrayList<>();

    public ArrayList<IRBlock>dfn=new ArrayList<>();
    public HashMap<IRBlock,Integer>dfsID=new HashMap<>();
    public HashMap<IRBlock,IRBlock>Fa=new HashMap<>();
    public HashMap<IRBlock,IRBlock>sDom=new HashMap<>();
    public HashMap<IRBlock,IRBlock>union=new HashMap<>();
    public HashMap<IRBlock,IRBlock>minVer=new HashMap<>();
    public int cnt=0;

    public DomAnalysis(Function func){
        this.func=func;
    }
    public void solve(){
        bucket=new ArrayList<>();
        dfn=new ArrayList<>();
        cnt=0;
        dfn.add(null);
        dfs(func.inBlock);
        Fa.put(func.inBlock,null);
        for(int i=0; i<=cnt; i++)
            bucket.add(new ArrayList<>());
        for(int i=cnt; i>1; i--){
            var nowBlock=dfn.get(i);
            nowBlock.fas.forEach(fa->{
                var evalBlock=evaluate(fa);
                if(dfsID.get(sDom.get(nowBlock))>dfsID.get(sDom.get(evalBlock)))
                    sDom.put(nowBlock,sDom.get(evalBlock));
            });
            bucket.get(dfsID.get(sDom.get(nowBlock))).add(nowBlock);
            var fa=Fa.get(nowBlock);
            union.put(nowBlock,fa);
            bucket.get(dfsID.get(fa)).forEach(block -> {
                var evalBlock=evaluate(block);
                if(dfsID.get(sDom.get(evalBlock))<dfsID.get(fa))
                    block.imDom=evalBlock;
                else
                    block.imDom=fa;
            });
            bucket.get(dfsID.get(fa)).clear();
        }
        for(int i=2; i<=cnt; i++){
            var nowBlock=dfn.get(i);
            if(nowBlock.imDom!=sDom.get(nowBlock))
                nowBlock.imDom=nowBlock.imDom.imDom;
        }
        int size=dfn.size();
        for(int i=1; i<size; i++){
            var block =dfn.get(i);
            if(block.fas.size()>=2){
                for(var nowBlock:block.fas){
                    while(nowBlock!=block.imDom){
                        nowBlock.domBlocks.add(block);
                        nowBlock=nowBlock.imDom;
                    }
                }
            }
        }
    }
    public void dfs(IRBlock block){
        if(dfsID.containsKey(block))
            return;
        block.imDom=null;
        block.domBlocks.clear();
        dfsID.put(block,++cnt);
        dfn.add(block);
        sDom.put(block,block);
        union.put(block,block);
        minVer.put(block,block);
        block.sons.forEach(son->{
            if(!dfsID.containsKey(son)){
                dfs(son);
                Fa.put(son,block);
            }
        });
    }
    public IRBlock evaluate(IRBlock block){
        if(union.get(block)!=union.get(union.get(block))){
            if(dfsID.get(sDom.get(minVer.get(block)))>dfsID.get(sDom.get(evaluate(union.get(block)))))
                minVer.put(block,evaluate(union.get(block)));
            union.put(block,union.get(union.get(block)));
        }
        return minVer.get(block);
    }
}
