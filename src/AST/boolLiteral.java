package AST;
import Util.position;
public class boolLiteral extends ExprNode{
    public boolean value;
    public boolLiteral(boolean value, position pos){
        super(pos,false);
        this.value=value;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
