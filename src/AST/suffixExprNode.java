package AST;
import Util.Type;
import Util.position;
import java.util.ArrayList;
public class suffixExprNode extends ExprNode{
    public enum suffixOpType {
        selfplus, selfminus
    }
    public suffixOpType opcode;
    public ExprNode expr;
    public suffixExprNode(suffixOpType opcode, ExprNode expr, position pos){
        super(pos,false);
        this.opcode=opcode;
        this.expr=expr;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
