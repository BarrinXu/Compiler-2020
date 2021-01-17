package AST;
import Util.position;
public class funcExpr extends ExprNode{
    public String name;
    public funcExpr(String name, position pos){
        super(pos,false);
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
