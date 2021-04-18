package BackEnd;

import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.Jump;
import MIR.IRInst.Move;
import MIR.IROperand.Register;
import MIR.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SolvePhi {
    public Root IRRoot;
    public SolvePhi(Root IRRoot){
        this.IRRoot=IRRoot;
    }

    public void solve()
    {
        IRRoot.functions.forEach((name,func)->solveFunc(func));
    }

    public void solveFunc(Function func){
        HashSet<BlockFaSonPair> VIPBlockFaSonPairs =new HashSet<>();
        func.blocks.forEach(block -> {
            if(block.sons.size()>1)
                block.sons.forEach(son->{
                    if(son.fas.size()>1)
                        VIPBlockFaSonPairs.add(new BlockFaSonPair(block,son));
                });
        });
        VIPBlockFaSonPairs.forEach(blockFaSonPair -> {
            IRBlock intermediateBlock=new IRBlock("intermediateBlock");
            func.blocks.add(intermediateBlock);

            intermediateBlock.setTerminate(new Jump(intermediateBlock, blockFaSonPair.son));
            blockFaSonPair.son.phiInstMap.forEach((reg, phi)->{
                int size=phi.blocks.size();
                for(int i=0; i<size; i++)
                    if(phi.blocks.get(i)== blockFaSonPair.fa)
                        phi.blocks.set(i,intermediateBlock);
            });
            blockFaSonPair.fa.modifySon(blockFaSonPair.son, intermediateBlock);
        });
        HashMap<IRBlock,parallelCopy> blockMap=new HashMap<>();
        func.blocks.forEach(block -> {
            blockMap.put(block,new parallelCopy());
        });
        func.blocks.forEach(block -> {
            block.phiInstMap.forEach((reg,phi)->{
                int size=phi.blocks.size();
                for(int i=0; i<size; i++){
                    IRBlock oriBlock=phi.blocks.get(i);
                    blockMap.get(oriBlock).addMove(new Move(reg,oriBlock,phi.operands.get(i),false));
                }
            });
        });
        blockMap.forEach(this::solveBlock);
        //may be done

        //compress direct jump
        ArrayList<IRBlock>onlyJumpBlocks=new ArrayList<>();
        func.blocks.forEach(block -> {
            if(block!=func.inBlock&&block.head instanceof Jump)
                onlyJumpBlocks.add(block);
        });
        onlyJumpBlocks.forEach(block -> {
            var destBlock=((Jump)block.head).destBlock;
            destBlock.fas.remove(block);
            block.fas.forEach(fa->fa.modifyBranchDestOfDirectJumpCompress(block,destBlock));
        });
        func.blocks.removeAll(onlyJumpBlocks);
    }
    public void solveBlock(IRBlock block,parallelCopy copy){
        boolean done=false;
        while(!done){
            boolean found=false;
            for(Iterator<Move>it=copy.copys.iterator(); it.hasNext();) {
                var inst=it.next();
                if(!copy.regMap.containsKey(inst.reg)){
                    it.remove();
                    if(inst.oriOperand instanceof Register){
                        int cnt=copy.regMap.get(inst.oriOperand);
                        if(cnt>1)
                            copy.regMap.put(inst.oriOperand,cnt-1);
                        else
                            copy.regMap.remove(inst.oriOperand);
                    }
                    block.addInstBack(new Move(inst.reg,block,inst.oriOperand,true));
                    found=true;
                }
            }
            if(!found){
                for(var inst:copy.copys){
                    if(inst.oriOperand!= inst.reg){
                        Register oriBackup=new Register(inst.oriOperand.type,"mirror_"+inst.oriOperand.getIdentity());
                        block.addInstBack(new Move(oriBackup,block,inst.oriOperand,true));
                        copy.regMap.remove(inst.oriOperand);
                        copy.copys.forEach(updInst->{
                            updInst.modifyReg((Register) inst.oriOperand,oriBackup);
                        });
                        break;
                    }
                }
            }
            done=true;
            for(var inst:copy.copys)
                if(inst.oriOperand!=inst.reg)
                {
                    done=false;
                    break;
                }
        }

    }
}
