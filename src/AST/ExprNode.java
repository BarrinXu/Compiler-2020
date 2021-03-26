package AST;

import MIR.IRBlock;
import MIR.IROperand.IRBaseOperand;
import Util.Type;
import Util.position;

public abstract class ExprNode extends ASTNode {
    public Type type;
    public boolean isAssignable;
    public IRBaseOperand operand;

    public IRBlock thenBlock=null,elseBlock=null;

    public ExprNode(position pos, boolean isAssignable) {
        super(pos);
        this.isAssignable=isAssignable;
    }

}
