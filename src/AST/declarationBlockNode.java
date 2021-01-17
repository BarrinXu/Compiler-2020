package AST;
import Util.position;
import java.util.ArrayList;
public class declarationBlockNode extends StmtNode{
    public ArrayList<declarationNode> decls=new ArrayList<>();
    public declarationBlockNode(position pos){
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
