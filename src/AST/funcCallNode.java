package AST;
import Util.Type;
import Util.position;
import java.util.ArrayList;
public class funcCallNode extends ExprNode{
    public ArrayList<ExprNode> parameters;

    public ExprNode call;
    public funcCallNode(ExprNode call, ArrayList<ExprNode> parameters,position pos){
        super(pos,false);
        this.call=call;
        this.parameters=parameters;
    }

    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
