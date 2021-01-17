package AST;

import Util.Type;
import Util.position;
import java.util.ArrayList;


public class prefixExprNode extends ExprNode{
    public enum prefixOpType {
        selfplus, selfminus, plus, minus, tilde, not
    }
    public prefixOpType opcode;
    public ExprNode expr;
    public prefixExprNode(prefixOpType opcode, ExprNode expr, position pos){
        super(pos,opcode==prefixOpType.selfplus||opcode==prefixOpType.selfminus);
        this.opcode=opcode;
        this.expr=expr;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
