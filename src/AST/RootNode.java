package AST;

import Util.position;

import java.util.ArrayList;

public class RootNode extends ASTNode {
    //public FnRootNode fn;
    //public ArrayList<structDefNode> strDefs = new ArrayList<>();
    public ArrayList<declarationNode> globalVars=new ArrayList<>();
    public ArrayList<functionNode> functions=new ArrayList<>();
    public ArrayList<classNode> classes=new ArrayList<>();
    public ArrayList<ASTNode> nodeList=new ArrayList<>();
    /*public RootNode(position pos, FnRootNode fn) {
        super(pos);
        this.fn = fn;
    }*/
    public RootNode(position pos) {
        super(pos);

    }
    public void addDeclNode(declarationNode node){
        globalVars.add(node);
        nodeList.add(node);
    }
    public void addFuncNode(functionNode node){
        functions.add(node);
        nodeList.add(node);
    }
    public void addClassNode(classNode node){
        classes.add(node);
        nodeList.add(node);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
