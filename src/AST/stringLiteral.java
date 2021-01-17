package AST;
import Util.position;
public class stringLiteral extends ExprNode{
    public String value;
    public stringLiteral(String value, position pos){
        super(pos,false);
        this.value=value;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
