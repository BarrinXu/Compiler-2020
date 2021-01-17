package AST;
import Util.Type;
import Util.position;
import java.util.ArrayList;
public class memberNode extends ExprNode{
    public ExprNode call;
    public String member;
    public memberNode(ExprNode call,String member, position pos){
        super(pos,true);
        this.call=call;
        this.member=member;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}