package AST;

import Util.Type;
import Util.position;

public abstract class ExprNode extends ASTNode {
    public Type type;
    public boolean isAssignable;
    public ExprNode(position pos, boolean isAssignable) {
        super(pos);
        this.isAssignable=isAssignable;
    }

}
