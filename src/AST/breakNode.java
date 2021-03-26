package AST;
import Util.position;
public class breakNode extends StmtNode{
    public ASTNode dest;

    public breakNode(position pos){
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
