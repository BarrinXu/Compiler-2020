package AST;

import Util.position;

public class continueNode extends StmtNode{
    public ASTNode dest;
    public continueNode(position pos){
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
