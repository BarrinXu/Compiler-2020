// Generated from D:/Barrin/Documents/Computer System(I)/compiler/Yx-main/src/Parser\Yx.g4 by ANTLR 4.9
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link YxParser}.
 */
public interface YxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link YxParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(YxParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(YxParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void enterClassDef(YxParser.ClassDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#classDef}.
	 * @param ctx the parse tree
	 */
	void exitClassDef(YxParser.ClassDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#constructorDef}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDef(YxParser.ConstructorDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#constructorDef}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDef(YxParser.ConstructorDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#functionDef}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDef(YxParser.FunctionDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#functionDef}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDef(YxParser.FunctionDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 */
	void enterFunctionParameterDef(YxParser.FunctionParameterDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 */
	void exitFunctionParameterDef(YxParser.FunctionParameterDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(YxParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(YxParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#suite}.
	 * @param ctx the parse tree
	 */
	void enterSuite(YxParser.SuiteContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#suite}.
	 * @param ctx the parse tree
	 */
	void exitSuite(YxParser.SuiteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code block}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlock(YxParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code block}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlock(YxParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varDefStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDefStmt(YxParser.VarDefStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varDefStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDefStmt(YxParser.VarDefStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(YxParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(YxParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(YxParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(YxParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(YxParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(YxParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(YxParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(YxParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(YxParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(YxParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(YxParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(YxParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code pureExprStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterPureExprStmt(YxParser.PureExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pureExprStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitPureExprStmt(YxParser.PureExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterEmptyStmt(YxParser.EmptyStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitEmptyStmt(YxParser.EmptyStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#declarationStmt}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationStmt(YxParser.DeclarationStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#declarationStmt}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationStmt(YxParser.DeclarationStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(YxParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(YxParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code indexExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIndexExpr(YxParser.IndexExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code indexExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIndexExpr(YxParser.IndexExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixExpr(YxParser.PrefixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixExpr(YxParser.PrefixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberExpr(YxParser.MemberExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberExpr(YxParser.MemberExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code suffixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSuffixExpr(YxParser.SuffixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code suffixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSuffixExpr(YxParser.SuffixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAtomExpr(YxParser.AtomExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAtomExpr(YxParser.AtomExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(YxParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(YxParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpr(YxParser.AssignExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpr(YxParser.AssignExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code functionExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionExpr(YxParser.FunctionExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code functionExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionExpr(YxParser.FunctionExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#arraySemanticError}.
	 * @param ctx the parse tree
	 */
	void enterArraySemanticError(YxParser.ArraySemanticErrorContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#arraySemanticError}.
	 * @param ctx the parse tree
	 */
	void exitArraySemanticError(YxParser.ArraySemanticErrorContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteral(YxParser.ArrayLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#arrayLiteral}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteral(YxParser.ArrayLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#constructorCall}.
	 * @param ctx the parse tree
	 */
	void enterConstructorCall(YxParser.ConstructorCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#constructorCall}.
	 * @param ctx the parse tree
	 */
	void exitConstructorCall(YxParser.ConstructorCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#exprOrvarDef}.
	 * @param ctx the parse tree
	 */
	void enterExprOrvarDef(YxParser.ExprOrvarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#exprOrvarDef}.
	 * @param ctx the parse tree
	 */
	void exitExprOrvarDef(YxParser.ExprOrvarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void enterVarDef(YxParser.VarDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#varDef}.
	 * @param ctx the parse tree
	 */
	void exitVarDef(YxParser.VarDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclaration(YxParser.VarDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclaration(YxParser.VarDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void enterReturnType(YxParser.ReturnTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#returnType}.
	 * @param ctx the parse tree
	 */
	void exitReturnType(YxParser.ReturnTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterVarType(YxParser.VarTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitVarType(YxParser.VarTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#builtinType}.
	 * @param ctx the parse tree
	 */
	void enterBuiltinType(YxParser.BuiltinTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#builtinType}.
	 * @param ctx the parse tree
	 */
	void exitBuiltinType(YxParser.BuiltinTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(YxParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(YxParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link YxParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(YxParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link YxParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(YxParser.LiteralContext ctx);
}