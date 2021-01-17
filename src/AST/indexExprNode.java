package AST;
import Util.position;
public class indexExprNode extends ExprNode{
    public ExprNode lhs,rhs;
    public indexExprNode(ExprNode lhs, ExprNode rhs, position pos){
        super(pos,lhs.isAssignable);
        this.lhs=lhs;
        this.rhs=rhs;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
