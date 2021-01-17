// Generated from D:/Barrin/Documents/Computer System(I)/compiler/Yx-main/src/Parser\Yx.g4 by ANTLR 4.9
package Parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link YxParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface YxVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link YxParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(YxParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#classDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDef(YxParser.ClassDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#constructorDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDef(YxParser.ConstructorDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#functionDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDef(YxParser.FunctionDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#functionParameterDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParameterDef(YxParser.FunctionParameterDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(YxParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#suite}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuite(YxParser.SuiteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code block}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(YxParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varDefStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDefStmt(YxParser.VarDefStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code whileStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(YxParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(YxParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code forStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(YxParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code breakStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(YxParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code continueStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(YxParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code returnStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(YxParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code pureExprStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPureExprStmt(YxParser.PureExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code emptyStmt}
	 * labeled alternative in {@link YxParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyStmt(YxParser.EmptyStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#declarationStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclarationStmt(YxParser.DeclarationStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code newExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNewExpr(YxParser.NewExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code indexExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexExpr(YxParser.IndexExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code prefixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixExpr(YxParser.PrefixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code memberExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberExpr(YxParser.MemberExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code suffixExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuffixExpr(YxParser.SuffixExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomExpr(YxParser.AtomExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code binaryExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBinaryExpr(YxParser.BinaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignExpr(YxParser.AssignExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code functionExpr}
	 * labeled alternative in {@link YxParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionExpr(YxParser.FunctionExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#arraySemanticError}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArraySemanticError(YxParser.ArraySemanticErrorContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#arrayLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayLiteral(YxParser.ArrayLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#constructorCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorCall(YxParser.ConstructorCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#exprOrvarDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprOrvarDef(YxParser.ExprOrvarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#varDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDef(YxParser.VarDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclaration(YxParser.VarDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#returnType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnType(YxParser.ReturnTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarType(YxParser.VarTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#builtinType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBuiltinType(YxParser.BuiltinTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(YxParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link YxParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(YxParser.LiteralContext ctx);
}