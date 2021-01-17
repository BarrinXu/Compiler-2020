package AST;

import Util.position;

public class typeNode extends ASTNode{
    public String name;
    public int dim;

    public typeNode(String name, int dim, position pos){
        super(pos);
        this.name=name;
        this.dim=dim;
    }
    @Override
    public void accept(ASTVisitor visitor){
        visitor.visit(this);
    }
}
