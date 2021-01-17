package AST;
import Util.Type;
import Util.position;
import Util.varEntity;

public class declarationNode extends StmtNode{
    public typeNode type;
    public String name;
    public ExprNode expr=null;
    public varEntity entity;
    public declarationNode(typeNode type, String name, position pos){
        super(pos);
        this.type=type;
        this.name=name;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
