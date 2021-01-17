package AST;

import Util.position;

public class whileStmtNode extends StmtNode{
    public ExprNode condition;
    public StmtNode statement;

    public whileStmtNode(ExprNode condition, StmtNode statement, position pos){
        super(pos);
        this.condition=condition;
        this.statement=statement;
    }

    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
