package AST;

import MIR.IRBlock;
import Util.position;

public class forStmtNode extends StmtNode{
    public ExprNode initExpr, condExpr, updExpr;
    public StmtNode loopBody;

    public IRBlock updBlock, destBlock;

    public forStmtNode(position pos) {
        super(pos);
    }

    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
