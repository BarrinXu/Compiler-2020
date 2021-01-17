package AST;

import Util.position;

public class binaryExprNode extends ExprNode {
    public ExprNode lhs, rhs;
    public enum binaryOpType {
        mul, div, mod, and, caret, leftshift, rightshift, or, minus, plus, less, lessequal, greater, greaterequal, andand, oror, equal, notequal
    }
    public binaryOpType opCode;

    public binaryExprNode(ExprNode lhs, ExprNode rhs, binaryOpType opCode, position pos) {
        super(pos,false);
        this.lhs = lhs;
        this.rhs = rhs;
        this.opCode = opCode;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
