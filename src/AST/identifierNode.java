package AST;

import Util.position;

public class identifierNode extends ExprNode{
    public String name;
    public identifierNode(String name, position pos){
        super(pos, true);
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
