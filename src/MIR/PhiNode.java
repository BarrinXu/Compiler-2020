package MIR;

import MIR.IROperand.IRBaseOperand;

import java.util.ArrayList;

public class PhiNode {
    public ArrayList<IRBaseOperand> vals=new ArrayList<>();
    public ArrayList<IRBlock> blocks=new ArrayList<>();
}
