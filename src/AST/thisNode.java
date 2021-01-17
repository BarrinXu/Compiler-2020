package AST;

import Util.position;

public class thisNode extends ExprNode{
    public thisNode(position pos){
        super(pos,false);
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
