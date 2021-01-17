package Frontend;

import AST.*;
import Parser.YxBaseVisitor;
import Parser.YxParser;
import Util.Type;
import Util.position;
import AST.binaryExprNode.binaryOpType;
import AST.prefixExprNode.prefixOpType;

import AST.suffixExprNode.suffixOpType;
import Util.error.semanticError;

import java.util.ArrayList;

public class ASTBuilder extends YxBaseVisitor<ASTNode> {

    /*private globalScope gScope;
    public ASTBuilder(globalScope gScope) {
        this.gScope = gScope;
    }

    Type intType, boolType, stringType, nullType;*/

    private void solveVarDef(YxParser.VarDefContext ctx, RootNode node){
        typeNode type=solveVarType(ctx.varType());
        for(var subctx:ctx.varDeclaration()){
            var tmp=(declarationNode)visitVarDeclaration(subctx);
            tmp.type=type;
            node.addDeclNode(tmp);
        }
    }

    private void solveVarDef(YxParser.VarDefContext ctx, ArrayList<declarationNode> list){
        typeNode type=solveVarType(ctx.varType());
        for(var subctx:ctx.varDeclaration()){
            var tmp=(declarationNode)visitVarDeclaration(subctx);
            tmp.type=type;
            list.add(tmp);
        }
    }

    private typeNode solveVarType(YxParser.VarTypeContext ctx){
        int dim=0;
        if(ctx.LeftBracket()!=null)
            dim=ctx.LeftBracket().size();
        String typeName;
        if(ctx.builtinType()!=null)
            typeName=ctx.builtinType().getText();
        else
            typeName=ctx.Identifier().getText();
        return new typeNode(typeName,dim,new position(ctx));
        /*if(ctx.varType()!=null)
            return new typeNode(solveVarType(ctx.varType()),ctx.LeftBracket().size());
        else if(ctx.builtinType()!=null){
            if(ctx.builtinType().Int()!=null)
                return intType;
            else if(ctx.builtinType().Bool()!=null)
                return boolType;
            else if(ctx.builtinType().String()!=null)
                return stringType;
            else
                return null;
        }
        else return null;//how to do with class?*/
    }

    private ArrayList<declarationNode> solveFunctionParameterDef(YxParser.FunctionParameterDefContext ctx){
        var ret=new ArrayList<declarationNode>();
        for(int i=0; i<ctx.Identifier().size(); i++){
            ret.add(new declarationNode(solveVarType(ctx.varType(i)), ctx.Identifier(i).getText(), new position(ctx)));
        }
        return ret;
    }

    @Override public ASTNode visitProgram(YxParser.ProgramContext ctx) {
        var node=new RootNode(new position (ctx));
        for(var subctx:ctx.children){
            if(subctx instanceof YxParser.ClassDefContext)
                node.addClassNode((classNode)visitClassDef((YxParser.ClassDefContext)subctx));
            else if(subctx instanceof YxParser.FunctionDefContext)
                node.addFuncNode((functionNode)visitFunctionDef((YxParser.FunctionDefContext)subctx));
            else
                solveVarDef(((YxParser.DeclarationStmtContext)subctx).varDef(),node);
        }
        /*ctx.declarationStmt().forEach();
        RootNode root = new RootNode(new position(ctx), (FnRootNode)visit(ctx.mainFn()));
        ctx.classDef().forEach(cd -> root.strDefs.add((structDefNode) visit(cd)));*/
        return node;
    }

    @Override public ASTNode visitClassDef(YxParser.ClassDefContext ctx) {
        var node=new classNode(ctx.Identifier().getText(),new position(ctx));
        for(var subctx:ctx.declarationStmt())
            solveVarDef(subctx.varDef(), node.members);
        for(var subctx:ctx.functionDef()) {
            var tmp=(functionNode)visitFunctionDef(subctx);
            tmp.isMethod=true;
            node.methods.add(tmp);
        }
        for(var subctx:ctx.constructorDef()) {
            var tmp=(functionNode)visitConstructorDef(subctx);
            tmp.isMethod=true;
            node.constructor=tmp;//assume only one!
        }
        return node;
        /*structDefNode structDef = new structDefNode(new position(ctx), ctx.Identifier().toString());
        ctx.varDef().forEach(vd -> structDef.varDefs.add((varDefStmtNode) visit(vd)));
        return structDef;*/
    }

    @Override public ASTNode visitConstructorDef(YxParser.ConstructorDefContext ctx){
        var node=new functionNode(ctx.Identifier().getText(),new position(ctx));
        node.returnType=null;
        node.suite=(suiteNode)visitSuite(ctx.suite());
        node.isConstructor=true;
        return node;
    }

    @Override public ASTNode visitFunctionDef(YxParser.FunctionDefContext ctx){
        var node=new functionNode(ctx.Identifier().getText(), new position(ctx));

        node.returnType=(ctx.returnType().Void()!=null)?(new typeNode("void",0,new position(ctx))):solveVarType(ctx.returnType().varType());
        node.suite=(suiteNode)visitSuite(ctx.suite());
        node.parameters=solveFunctionParameterDef(ctx.functionParameterDef());
        return node;
    }

    /*@Override public ASTNode visitMainFn(YxParser.MainFnContext ctx) {
        FnRootNode root = new FnRootNode(new position(ctx));
        intType = root.intType;
        boolType = root.boolType;
        gScope.addType("int", intType, root.pos);
        gScope.addType("bool", boolType, root.pos);

        if (ctx.suite() != null) {
            for (ParserRuleContext stmt : ctx.suite().statement())
                root.stmts.add((StmtNode) visit(stmt));
        }
        return root;
    }*/

    /*@Override public ASTNode visitVarDef(YxParser.VarDefContext ctx) {
        ExprNode expr = null;
        String typeName;
        if (ctx.type().Int() != null) typeName = ctx.type().Int().toString();
        else typeName = ctx.type().Identifier().toString();
        if (ctx.expression() != null) expr = (ExprNode)visit(ctx.expression());

        return new varDefStmtNode(typeName,
                    ctx.Identifier().toString(),
                    expr, new position(ctx));
    }*/

    @Override public ASTNode visitSuite(YxParser.SuiteContext ctx) {
        var node = new suiteNode(new position(ctx));

        if (!ctx.statement().isEmpty()) {
            for (var subctx : ctx.statement()) {
                var tmp = (StmtNode) visit(subctx);
                if (tmp != null) node.statements.add(tmp);
            }
        }
        return node;
    }

    @Override public ASTNode visitBlock(YxParser.BlockContext ctx) {
        return visit(ctx.suite());
    }

    @Override public ASTNode visitVarDefStmt(YxParser.VarDefStmtContext ctx) {

        return visit(ctx.declarationStmt());
    }

    @Override public ASTNode visitWhileStmt(YxParser.WhileStmtContext ctx) {
        var node=new forStmtNode(new position(ctx));
        node.initExpr=null;
        node.condExpr=(ExprNode) visit(ctx.expression());
        node.updExpr=null;
        node.loopBody=(ctx.statement()==null)?null:(StmtNode)visit(ctx.statement());
        return node;
    }


    @Override public ASTNode visitIfStmt(YxParser.IfStmtContext ctx) {
        StmtNode thenStmt = (StmtNode)visit(ctx.trueStmt), elseStmt = null;
        ExprNode condition = (ExprNode)visit(ctx.expression());
        if (ctx.falseStmt != null) elseStmt = (StmtNode)visit(ctx.falseStmt);
        return new ifStmtNode(condition, thenStmt, elseStmt, new position(ctx));
    }

    @Override public ASTNode visitForStmt(YxParser.ForStmtContext ctx) {
        var node=new forStmtNode(new position(ctx));
        if(ctx.initExp!=null){
            //assume only for(a=1;;), not for(int a=1;;)
            node.initExpr=(ExprNode)visit(ctx.initExp.expression());
        }
        node.condExpr=(ctx.condExp==null)?null:(ExprNode)visit(ctx.condExp);
        node.updExpr=(ctx.updExp==null)?null:(ExprNode)visit(ctx.updExp);
        node.loopBody=(ctx.statement()==null)?null:(StmtNode)visit(ctx.statement());
        return node;
    }

    @Override public ASTNode visitBreakStmt(YxParser.BreakStmtContext ctx) {
        return new breakNode(new position(ctx));
    }

    @Override public ASTNode visitContinueStmt(YxParser.ContinueStmtContext ctx) {
        return new continueNode(new position(ctx));
    }


    @Override public ASTNode visitReturnStmt(YxParser.ReturnStmtContext ctx) {
        ExprNode value = null;
        if (ctx.expression() != null) value = (ExprNode) visit(ctx.expression());
        return new returnStmtNode(value, new position(ctx));
    }

    @Override public ASTNode visitPureExprStmt(YxParser.PureExprStmtContext ctx) {
        return new exprStmtNode((ExprNode) visit(ctx.expression()), new position(ctx));
    }

    @Override public ASTNode visitEmptyStmt(YxParser.EmptyStmtContext ctx) {
        return null;
    }

    @Override public ASTNode visitDeclarationStmt(YxParser.DeclarationStmtContext ctx) {
        var node=new declarationBlockNode(new position(ctx));
        solveVarDef(ctx.varDef(),node.decls);
        return node;
    }

    @Override public ASTNode visitNewExpr(YxParser.NewExprContext ctx) {
        if(ctx.arraySemanticError()!=null) {
            throw new semanticError("",new position(ctx));
        }
        if(ctx.Identifier()!=null)
            return new newExprNode(new typeNode(ctx.Identifier().getText(),0,new position(ctx)),new ArrayList<>(),new position(ctx));
        if(ctx.constructorCall()!=null)
            return new newExprNode(new typeNode(ctx.constructorCall().Identifier().getText(),0,new position(ctx)),new ArrayList<>(),new position(ctx));
        return visit(ctx.arrayLiteral());
    }

    @Override public ASTNode visitIndexExpr(YxParser.IndexExprContext ctx) {
        return new indexExprNode((ExprNode)visit(ctx.expression(0)),(ExprNode)visit(ctx.expression(1)),new position(ctx));
    }

    @Override public ASTNode visitPrefixExpr(YxParser.PrefixExprContext ctx) {
        ExprNode node=null;
        if(ctx.SelfPlus()!=null)
            node=new prefixExprNode(prefixOpType.selfplus,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.SelfMinus()!=null)
            node=new prefixExprNode(prefixOpType.selfminus,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.Plus()!=null)
            node=new prefixExprNode(prefixOpType.plus,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.Minus()!=null)
            node=new prefixExprNode(prefixOpType.minus,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.Not()!=null)
            node=new prefixExprNode(prefixOpType.not,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.Tilde()!=null)
            node=new prefixExprNode(prefixOpType.tilde,(ExprNode)visit(ctx.expression()),new position(ctx));
        return node;
    }

    @Override public ASTNode visitMemberExpr(YxParser.MemberExprContext ctx) {
        return new memberNode((ExprNode)visit(ctx.expression()),ctx.Identifier().getText(),new position(ctx));
    }

    @Override public ASTNode visitSuffixExpr(YxParser.SuffixExprContext ctx) {
        ExprNode node=null;
        if(ctx.SelfPlus()!=null)
            node=new suffixExprNode(suffixOpType.selfplus,(ExprNode)visit(ctx.expression()),new position(ctx));
        else if(ctx.SelfMinus()!=null)
            node=new suffixExprNode(suffixOpType.selfminus,(ExprNode)visit(ctx.expression()),new position(ctx));
        return node;
    }


    @Override public ASTNode visitAtomExpr(YxParser.AtomExprContext ctx) {
        return visit(ctx.primary());
    }

    @Override public ASTNode visitBinaryExpr(YxParser.BinaryExprContext ctx) {
        ExprNode lhs = (ExprNode) visit(ctx.expression(0)),
                 rhs = (ExprNode) visit(ctx.expression(1));
        ExprNode node=null;
        if(ctx.Mul()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.mul,new position(ctx));
        else if(ctx.Div()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.div,new position(ctx));
        else if(ctx.Mod()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.mod,new position(ctx));
        else if(ctx.Plus()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.plus,new position(ctx));
        else if(ctx.Minus()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.minus,new position(ctx));
        else if(ctx.LeftShift()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.leftshift,new position(ctx));
        else if(ctx.RightShift()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.rightshift,new position(ctx));
        else if(ctx.Less()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.less,new position(ctx));
        else if(ctx.LessEqual()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.lessequal,new position(ctx));
        else if(ctx.Greater()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.greater,new position(ctx));
        else if(ctx.GreaterEqual()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.greaterequal,new position(ctx));
        else if(ctx.Equal()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.equal,new position(ctx));
        else if(ctx.NotEqual()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.notequal,new position(ctx));
        else if(ctx.And()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.and,new position(ctx));
        else if(ctx.Caret()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.caret,new position(ctx));
        else if(ctx.Or()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.or,new position(ctx));
        else if(ctx.AndAnd()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.andand,new position(ctx));
        else if(ctx.OrOr()!=null)
            node=new binaryExprNode(lhs,rhs,binaryOpType.oror,new position(ctx));
        return node;


        /*binaryOpType biOp = ctx.Plus() != null ? binaryOpType.add :
                            (ctx.Minus() != null ? binaryOpType.sub : null);
        cmpOpType cmpOp = ctx.Equal() != null ? cmpOpType.eq :
                            (ctx.NotEqual() != null ? cmpOpType.neq : null);

        return biOp != null ? (new binaryExprNode(lhs, rhs, biOp, intType, new position(ctx))) :
                                (new cmpExprNode(lhs, rhs, cmpOp, boolType, new position(ctx)));*/
    }

    @Override public ASTNode visitAssignExpr(YxParser.AssignExprContext ctx) {
        ExprNode lhs = (ExprNode) visit(ctx.expression(0)),
                 rhs = (ExprNode) visit(ctx.expression(1));
        return new assignExprNode(lhs, rhs, rhs.isAssignable, new position(ctx));
    }

    @Override public ASTNode visitFunctionExpr(YxParser.FunctionExprContext ctx) {
        var call=(ExprNode)visit(ctx.expression());
        if(call instanceof identifierNode)
            call=new funcExpr(((identifierNode) call).name,call.pos);
        else if(call instanceof memberNode)
            call=new methodExpr(((memberNode) call).call,((memberNode) call).member,call.pos);
        else
            throw new semanticError("",call.pos);
        return new funcCallNode(call,ctx.expressionList()==null?null:solveExpressionList(ctx.expressionList()),new position(ctx));
    }

    public ArrayList<ExprNode> solveExpressionList(YxParser.ExpressionListContext ctx){
        ArrayList<ExprNode> exprs=new ArrayList<>();
        if(ctx.expression()!=null){
            for(var subctx:ctx.expression())
                exprs.add((ExprNode)visit(subctx));
        }
        return exprs;
    }

    //only for new an array
    @Override public ASTNode visitArrayLiteral(YxParser.ArrayLiteralContext ctx) {
        String name;
        if(ctx.builtinType()!=null)
            name=ctx.builtinType().getText();
        else
            name=ctx.Identifier().getText();
        ArrayList<ExprNode> exprs=new ArrayList<>();
        if(ctx.expression()!=null){
            for(var subctx:ctx.expression())
                exprs.add((ExprNode)visit(subctx));
        }
        return new newExprNode(new typeNode(name,ctx.LeftBracket().size(),new position(ctx)),exprs,new position(ctx));
    }

    /*@Override public ASTNode visitConstructorCall(YxParser.ConstructorCallContext ctx) {
        var node=new funcCallNode(new identifierNode(ctx.Identifier().getText(),new position(ctx)),new position(ctx));
        //assume no parameters

        return node;
    }*/

    @Override public ASTNode visitVarDeclaration(YxParser.VarDeclarationContext ctx) {
        var node=new declarationNode(null,ctx.Identifier().getText(),new position(ctx));
        if(ctx.expression()!=null)
            node.expr=(ExprNode)visit(ctx.expression());
        return node;
    }


    @Override public ASTNode visitPrimary(YxParser.PrimaryContext ctx) {
        if (ctx.expression() != null) return visit(ctx.expression());
        else if (ctx.literal() != null) return visit(ctx.literal());
        else return new identifierNode(ctx.Identifier().getText(), new position(ctx.Identifier()));
    }

    @Override public ASTNode visitLiteral(YxParser.LiteralContext ctx) {
        if(ctx.This()!=null)
            return new thisNode(new position(ctx));
        else if(ctx.DecimalInteger()!=null)
            return new intLiteral(Integer.parseInt(ctx.DecimalInteger().getText()),new position(ctx));
        else if(ctx.StringType()!=null)
            return new stringLiteral(ctx.StringType().getText(),new position(ctx));
        else if(ctx.Null()!=null)
            return new nullLiteral(new position(ctx));
        else if(ctx.True()!=null)
            return new boolLiteral(true,new position(ctx));
        else if(ctx.False()!=null)
            return new boolLiteral(false,new position(ctx));
        else throw new semanticError("",new position(ctx));
    }

}
