package AST;

import Util.Scope;
import Util.position;

abstract public class ASTNode {
    public position pos;
    public Scope scope;
    public ASTNode(position pos) {
        this.pos = pos;
    }

    abstract public void accept(ASTVisitor visitor);
}
