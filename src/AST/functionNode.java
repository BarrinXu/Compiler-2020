package AST;
import Util.Type;
import Util.funcDecl;
import Util.position;
import java.util.ArrayList;
public class functionNode extends ASTNode{
    public typeNode returnType;
    public String name;
    public ArrayList<declarationNode> parameters=new ArrayList<>();
    public suiteNode suite;
    public funcDecl decl;
    public boolean isConstructor=false, isMethod=false;
    public functionNode(String name, position pos){
        super(pos);
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
