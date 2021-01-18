package Frontend;

import AST.*;
import Util.*;
import Util.error.*;
import java.util.HashMap;

public class SymbolCollector implements ASTVisitor {
    private globalScope gScope;
    Scope currentScope=null;
    public SymbolCollector(globalScope gScope) {
        this.gScope = gScope;
    }
    @Override
    public void visit(RootNode it) {
        currentScope=gScope;
        it.nodeList.forEach(node -> node.accept(this));
    }

    /*@Override public void visit(structDefNode it) {
        Type struct = new Type();
        struct.members = new HashMap<>();
        it.varDefs.forEach(vd -> vd.accept(this));
        gScope.addType(it.name, struct, it.pos);
    }*/

    @Override public void visit(classNode it) {
        if(!(currentScope instanceof globalScope))
            throw new internalError("",it.pos);
        var type=new classType(it.name);
        currentScope=new Scope(currentScope);
        it.members.forEach(member->member.accept(this));
        it.methods.forEach(method->method.accept(this));
        if(it.constructor!=null)
            it.constructor.accept(this);
        type.localScope=currentScope;
        currentScope=currentScope.faScope;
        gScope.defineClass(it.name,type,it.pos);
        if(gScope.haveMethod(it.name,false))
            throw new semanticError("",it.pos);
    }

    @Override
    public void visit(functionNode it) {
        var type=new funcDecl(it.name);
        it.decl=type;
        if(it.isConstructor)
            currentScope.defConstructor(type,it.pos);
        else
            currentScope.defMethod(it.name,type,it.pos);
    }

    @Override public void visit(returnStmtNode it) {}

    @Override public void visit(exprStmtNode it) {}
    @Override public void visit(ifStmtNode it) {}
    @Override public void visit(assignExprNode it) {}
    @Override public void visit(binaryExprNode it) {}


    @Override
    public void visit(whileStmtNode it) {

    }

    @Override
    public void visit(forStmtNode it) {

    }


    @Override
    public void visit(declarationNode it) {

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
