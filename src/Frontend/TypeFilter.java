package Frontend;

import AST.*;
import Util.*;
import Util.error.internalError;
import Util.error.semanticError;

public class TypeFilter implements ASTVisitor {
    globalScope gScope;
    Scope curScope;

    public TypeFilter(globalScope gScope){
        this.gScope=gScope;
    }

    @Override
    public void visit(RootNode it) {
        curScope=gScope;
        if(!it.nodeList.isEmpty())
            it.nodeList.forEach(node->{if(!(node instanceof declarationNode)) node.accept(this);});
    }

    @Override
    public void visit(functionNode it) {
        funcDecl func=it.decl;
        if(it.isConstructor)
            func.type=new constructorType();
        else
            func.type=gScope.makeType(it.returnType);
        curScope=new functionScope(curScope);
        it.parameters.forEach(node->node.accept(this));
        func.localScope=(functionScope)curScope;
        curScope=curScope.faScope;
    }

    @Override
    public void visit(classNode it) {
        classType defClass=(classType)gScope.getTypeFromName(it.name,it.pos);
        curScope=defClass.localScope;
        it.methods.forEach(node->node.accept(this));
        if(it.constructor!=null)
            it.constructor.accept(this);
        curScope=curScope.faScope;
    }

    @Override
    public void visit(declarationNode it) {
        varEntity parameter=new varEntity(it.name,gScope.makeType(it.type));
        if(parameter.type().isVoid())
            throw new semanticError("",it.pos);
        if(curScope instanceof functionScope){
            it.entity=parameter;
            ((functionScope) curScope).addParameter(parameter,it.pos);
        }
        else throw new internalError("",it.pos);
    }



    @Override
    public void visit(returnStmtNode it) {

    }


    @Override
    public void visit(exprStmtNode it) {

    }

    @Override
    public void visit(ifStmtNode it) {

    }

    @Override
    public void visit(assignExprNode it) {

    }

    @Override
    public void visit(binaryExprNode it) {

    }



    @Override
    public void visit(whileStmtNode it) {

    }

    @Override
    public void visit(forStmtNode it) {

    }


    @Override
    public void visit(suiteNode it) {

    }



    @Override
    public void visit(funcCallNode it) {

    }

    @Override
    public void visit(memberNode it) {

    }

    @Override
    public void visit(newExprNode it) {

    }

    @Override
    public void visit(prefixExprNode it) {

    }

    @Override
    public void visit(suffixExprNode it) {

    }

    @Override
    public void visit(declarationBlockNode it) {

    }

    @Override
    public void visit(breakNode it) {

    }

    @Override
    public void visit(continueNode it) {

    }

    @Override
    public void visit(indexExprNode it) {

    }


    @Override
    public void visit(identifierNode it) {

    }

    @Override
    public void visit(thisNode it) {

    }

    @Override
    public void visit(typeNode it) {

    }

    @Override
    public void visit(funcExpr it) {

    }

    @Override
    public void visit(methodExpr it) {

    }

    @Override
    public void visit(intLiteral it) {

    }

    @Override
    public void visit(stringLiteral it) {

    }

    @Override
    public void visit(nullLiteral it) {

    }

    @Override
    public void visit(boolLiteral it) {

    }
}
