package Frontend;

import AST.*;
import Util.*;
import Util.error.semanticError;

import java.util.ArrayList;
import java.util.Stack;

public class SemanticChecker implements ASTVisitor {
    public Scope curScope;
    public globalScope gScope;

    public classType currentClass=null;
    public Type currentReturnType=null;
    public functionNode currentFunction;
    public Stack<ASTNode> loop=new Stack<>();

    public boolean checkClassMember=false;
    public boolean haveReturn=false;

    public SemanticChecker(globalScope gScope) {
        curScope = this.gScope = gScope;
    }



    @Override
    public void visit(RootNode it) {
        curScope=gScope;
        if(!it.nodeList.isEmpty()){
            checkClassMember=true;
            it.nodeList.forEach(node->{if(node instanceof classNode) node.accept(this);});
            checkClassMember=false;
            it.nodeList.forEach(node->node.accept(this));
        }
        if(!gScope.haveMethod("main",true))
            throw new semanticError("",it.pos);
    }




    @Override
    public void visit(returnStmtNode it) {
        haveReturn=true;
        if (it.value != null) {
            it.value.accept(this);
            if (!currentReturnType.sameType(it.value.type))
                throw new semanticError("Semantic Error: type not match.", it.value.pos);
        }
        else if(!currentReturnType.isVoid())
            throw new semanticError("",it.pos);
    }


    @Override
    public void visit(exprStmtNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(ifStmtNode it) {
        it.condition.accept(this);
        if (!it.condition.type.isBool())
            throw new semanticError("Semantic Error: type not match. It should be bool",
                    it.condition.pos);
        if(it.thenStmt!=null) {
            curScope = new Scope(curScope);
            it.thenStmt.scope = curScope;
            it.thenStmt.accept(this);
            curScope = curScope.faScope;
        }
        if (it.elseStmt != null) {
            curScope=new Scope(curScope);
            it.elseStmt.scope=curScope;
            it.elseStmt.accept(this);
            curScope=curScope.faScope;
        }
    }

    @Override
    public void visit(assignExprNode it) {
        it.lhs.accept(this);
        it.rhs.accept(this);
        Type typel=it.lhs.type,typer=it.rhs.type;
        if (!typel.sameType(typer))
            throw new semanticError("Semantic Error: type not match. ", it.pos);
        it.type=typel;
        if (!it.lhs.isAssignable)
            throw new semanticError("Semantic Error: not assignable", it.lhs.pos);
    }

    @Override
    public void visit(binaryExprNode it) {
        it.lhs.accept(this);
        it.rhs.accept(this);
        Type typel=it.lhs.type,typer=it.rhs.type;
        var opCode=it.opCode.ordinal();
        if(opCode<9){
            if(!(typel.isInt()&&typer.isInt()))
                throw new semanticError("",it.pos);
            it.type=gScope.intInstance;
        }
        else if(opCode<14){
            if(!(typel.sameType(typer)&&(typel.isInt()||typel.sameType(gScope.types.get("string")))))
                throw new semanticError("",it.pos);
            if(opCode==9)
                it.type=typel;
            else
                it.type=gScope.boolInstance;
        }
        else if(opCode<16){
            if(!(typel.isBool()&&typer.isBool()))
                throw new semanticError("",it.pos);
            it.type=gScope.boolInstance;
        }
        else if(opCode<18){
            if(!typel.sameType(typer))
                throw new semanticError("",it.pos);
            it.type=gScope.boolInstance;
        }

    }


    @Override
    public void visit(whileStmtNode it) {

    }

    @Override
    public void visit(forStmtNode it) {
        //assume no for(int a...;;), only for(a=1;;)
        if(it.initExpr!=null)
            it.initExpr.accept(this);
        if(it.updExpr!=null)
            it.updExpr.accept(this);
        if(it.condExpr!=null){
            it.condExpr.accept(this);
            if(!it.condExpr.type.isBool())
                throw new semanticError("",it.condExpr.pos);
        }
        if(it.loopBody!=null) {
        curScope=new Scope(curScope);
        loop.push(it);
        it.loopBody.scope=curScope;
        it.loopBody.accept(this);
        loop.pop();
        curScope=curScope.faScope;
        }
    }

    @Override
    public void visit(declarationNode it) {
        varEntity entity=new varEntity(it.name,gScope.makeType(it.type));
        it.entity=entity;
        if(entity.type().isVoid())
            throw new semanticError("",it.pos);
        if(it.expr!=null){
            it.expr.accept(this);
            if(!it.expr.type.sameType(entity.type()))
                throw new semanticError("",it.pos);
        }
        curScope.defMember(it.name,entity,it.pos);
    }

    @Override
    public void visit(suiteNode it) {
        it.statements.forEach(node->{
            if(node instanceof suiteNode){
                curScope=new Scope(curScope);
                node.scope=curScope;
                node.accept(this);
                curScope=curScope.faScope;
            }
            else
                node.accept(this);
        });
    }

    @Override
    public void visit(functionNode it) {
        if(it.isConstructor){
            currentReturnType=gScope.voidInstance;
            if(!it.name.equals(currentClass.name))
                throw new semanticError("",it.pos);
        }
        else
            currentReturnType=it.decl.type;
        currentFunction=it;
        haveReturn=false;
        curScope=it.decl.localScope;
        it.suite.accept(this);
        curScope=curScope.faScope;
        if(it.name.equals("main")){
            haveReturn=true;
            if(!currentReturnType.isInt())
                throw new semanticError("",it.pos);
            if(it.parameters.size()>0)
                throw new semanticError("",it.pos);
        }
        if(!haveReturn&&!currentReturnType.isVoid())
            throw new semanticError("",it.pos);
        currentFunction=null;
    }

    @Override
    public void visit(classNode it) {
        classType defClass=(classType) gScope.getTypeFromName(it.name,it.pos);
        curScope=defClass.localScope;
        currentClass=defClass;
        if(checkClassMember)
            it.members.forEach(node->node.accept(this));
        if(!checkClassMember){
            it.methods.forEach(node->node.accept(this));
            if(it.constructor!=null)
                it.constructor.accept(this);
        }
        currentClass=null;
        curScope=curScope.faScope;
    }

    @Override
    public void visit(funcCallNode it) {
        it.call.accept(this);
        if(!(it.call.type instanceof funcDecl))
            throw new semanticError("",it.call.pos);
        funcDecl func=(funcDecl)it.call.type;
        ArrayList<varEntity> arguments=func.localScope.parameters;
        ArrayList<ExprNode> parameters=it.parameters;
        parameters.forEach(node->node.accept(this));
        if(arguments.size()!=parameters.size())
            throw new semanticError("",it.pos);
        for(int i=0; i<parameters.size(); i++){
            if(!(parameters.get(i).type.sameType(arguments.get(i).type())))
                throw new semanticError("",it.pos);
        }
        it.type=func.type;
    }

    @Override
    public void visit(memberNode it) {
        it.call.accept(this);
        if(!it.call.type.isClass())
            throw new semanticError("",it.pos);
        classType type=(classType)it.call.type;
        if(type.localScope.haveMember(it.member,false)){
            it.type=type.localScope.getMemberType(it.member,it.pos,false);
        }
        else
            throw new semanticError("",it.pos);
    }

    @Override
    public void visit(newExprNode it) {
        it.exprs.forEach(node->
        {
            node.accept(this);
            if(!node.type.isInt())
                throw new semanticError("",node.pos);
        });
        it.type=gScope.makeType(it.type_);
    }

    @Override
    public void visit(prefixExprNode it) {
        it.expr.accept(this);
        var opCode=it.opcode.ordinal();
        if(opCode<5){
            if(!it.expr.type.isInt())
                throw new semanticError("",it.pos);
            if(opCode<2)
                if(!it.expr.isAssignable)
                    throw new semanticError("",it.pos);
            it.type=gScope.intInstance;
        }
        else{
            it.type=gScope.boolInstance;
            if(!it.expr.type.isBool())
                throw new semanticError("",it.pos);
        }
    }

    @Override
    public void visit(suffixExprNode it) {
        it.expr.accept(this);
        if(!it.expr.type.isInt())
            throw new semanticError("",it.pos);
        if(!it.expr.isAssignable)
            throw new semanticError("",it.pos);
        it.type=gScope.intInstance;
    }

    @Override
    public void visit(declarationBlockNode it) {
        it.decls.forEach(node->node.accept(this));
    }

    @Override
    public void visit(breakNode it) {
        if(loop.isEmpty())
                throw new semanticError("",it.pos);
    }

    @Override
    public void visit(continueNode it) {
        if(loop.isEmpty())
            throw new semanticError("",it.pos);
    }

    @Override
    public void visit(indexExprNode it) {
        it.lhs.accept(this);
        it.rhs.accept(this);
        if(!it.rhs.type.isInt())
            throw new semanticError("",it.rhs.pos);
        if(it.lhs.type.dim()>1)
            it.type=new ArrayType(it.lhs.type);
        else if(it.lhs.type.dim()<1)
            throw new semanticError("",it.lhs.pos);
        else
            it.type=((ArrayType)it.lhs.type).baseType;
    }


    @Override
    public void visit(identifierNode it) {
        it.type=curScope.getMemberType(it.name,it.pos,true);
    }

    @Override
    public void visit(thisNode it) {
        if(currentClass==null)
            throw new semanticError("",it.pos);
        it.type=currentClass;
    }

    @Override
    public void visit(typeNode it) {

    }

    @Override
    public void visit(funcExpr it) {
        it.type=curScope.getMethodType(it.name,it.pos,true);
    }

    @Override
    public void visit(methodExpr it) {
        it.call.accept(this);
        if(it.call.type.isArray()){
            if(it.method.equals("size"))
                it.type=gScope.getMethodType("size",it.pos,false);
            else
                throw new semanticError("",it.pos);
        }
        else{
            if(!it.call.type.isClass())
                throw new semanticError("",it.pos);
            classType type=(classType) it.call.type;
            if(type.localScope.haveMethod(it.method,false))
                it.type=type.localScope.getMethodType(it.method,it.pos,false);
            else
                throw new semanticError("",it.pos);

        }
    }

    @Override
    public void visit(intLiteral it) {
        it.type=gScope.intInstance;
    }

    @Override
    public void visit(stringLiteral it) {
        it.type=gScope.getTypeFromName("string",it.pos);
    }

    @Override
    public void visit(nullLiteral it) {
        it.type=gScope.nullInstance;
    }

    @Override
    public void visit(boolLiteral it) {
        it.type=gScope.boolInstance;
    }

}
