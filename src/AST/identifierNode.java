package AST;

import Util.position;
import Util.varEntity;

public class identifierNode extends ExprNode{
    public String name;
    public varEntity entity;
    public identifierNode(String name, position pos){
        super(pos, true);
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
