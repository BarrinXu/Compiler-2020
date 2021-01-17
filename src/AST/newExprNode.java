package AST;
import Util.Type;
import Util.position;
import java.util.ArrayList;
public class newExprNode extends ExprNode{
    public typeNode type_;
    public ArrayList<ExprNode> exprs;
    public newExprNode(typeNode type,ArrayList<ExprNode>exprs,position pos){
        super(pos,true);
        this.type_=type;
        this.exprs=exprs;
    }
    @Override
    public  void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
