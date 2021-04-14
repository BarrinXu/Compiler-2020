import AST.RootNode;
import BackEnd.*;
import FrontEnd.ASTBuilder;
import FrontEnd.SemanticChecker;
import FrontEnd.SymbolCollector;
import FrontEnd.TypeFilter;
import MIR.Root;
import Optimize.Optimizer;
import Parser.YxLexer;
import Parser.YxParser;
import Util.YxErrorListener;
import Util.error.error;
import Util.globalScope;
import assembly.LRoot;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;


public class Main {
    public static void main(String[] args) throws Exception{
        boolean codegen=true;
        boolean doOptimize=false;
        if(args.length>0){
            for(var arg:args){
                switch (arg){
                    case "-semantic":codegen=false;break;
                    case "-optimize":doOptimize=true;break;
                }
            }
        }

        String name = "test.mx";
        InputStream input = System.in;
        //InputStream input=new FileInputStream(name);
        //PrintStream stream=new PrintStream("D:\\Barrin\\Documents\\Virtual Machines\\Ubuntu 64 位\\share\\test.s");
        PrintStream stream=new PrintStream("output.s");
        try {
            RootNode ASTRoot;
            globalScope gScope = new globalScope();

            YxLexer lexer = new YxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new YxErrorListener());
            YxParser parser = new YxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new YxErrorListener());
            ParseTree parseTreeRoot = parser.program();
            ASTBuilder astBuilder = new ASTBuilder();
            ASTRoot = (RootNode)astBuilder.visit(parseTreeRoot);

            Root IRRoot=new Root();

            new SymbolCollector(gScope,IRRoot).visit(ASTRoot);
            new TypeFilter(gScope).visit(ASTRoot);
            new SemanticChecker(gScope,IRRoot).visit(ASTRoot);
            if(codegen){
                new IRBuilder(gScope,IRRoot).visit(ASTRoot);

                new MemToReg(IRRoot).solve();
                doOptimize=true;
                if(doOptimize)
                    new Optimizer(IRRoot).solve();
                new SolvePhi(IRRoot).solve();
                LRoot lRoot=new InstSelection(IRRoot).solve();
                //new RegAlloc(lRoot).solve();
                //new PremiumRegAlloc(lRoot).solve();
                new UltimateRegAlloc(lRoot).solve();
                new PrintAsm(lRoot,stream).solve();
            }

        } catch (error er) {
            System.err.println(er.toString());
            throw new RuntimeException();
        }
    }
}