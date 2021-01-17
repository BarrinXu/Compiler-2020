package AST;
import Util.position;
public class intLiteral extends ExprNode{
    public int value;
    public intLiteral(int value, position pos){
        super(pos,false);
        this.value=value;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
