package AST;


import Util.position;
import java.util.ArrayList;

public class suiteNode extends StmtNode{
    public ArrayList<StmtNode> statements=new ArrayList<>();
    public suiteNode(position pos){
        super(pos);
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
