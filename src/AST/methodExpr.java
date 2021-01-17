package AST;
import Util.position;
public class methodExpr extends ExprNode{
    public ExprNode call;
    public String method;
    public methodExpr(ExprNode call, String method, position pos){
        super(pos,false);
        this.call=call;
        this.method=method;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
