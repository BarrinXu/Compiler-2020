package AST;

public interface ASTVisitor {
    void visit(RootNode it);
    //void visit(structDefNode it);

    void visit(returnStmtNode it);

    void visit(exprStmtNode it);
    void visit(ifStmtNode it);

    void visit(assignExprNode it);
    void visit(binaryExprNode it);



    void visit(forStmtNode it);


    void visit(declarationNode it);

    void visit(suiteNode it);

    void visit(functionNode it);

    void visit(classNode it);

    void visit(funcCallNode it);

    void visit(memberNode it);

    void visit(newExprNode it);

    void visit(prefixExprNode it);

    void visit(suffixExprNode it);

    void visit(declarationBlockNode it);

    void visit(breakNode it);

    void visit(continueNode it);

    void visit(indexExprNode it);

    void visit(identifierNode it);

    void visit(thisNode it);

    void visit(typeNode it);

    void visit(funcExpr it);

    void visit(methodExpr it);

    void visit(intLiteral it);

    void visit(stringLiteral it);

    void visit(nullLiteral it);

    void visit(boolLiteral it);
}
