package AST;
import Util.Type;
import Util.position;
import java.util.ArrayList;
public class classNode extends ASTNode{
    public ArrayList<declarationNode> members=new ArrayList<>();
    public ArrayList<functionNode> methods=new ArrayList<>();
    public functionNode constructor=null;
    public String name;
    public classNode(String name, position pos){
        super(pos);
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
