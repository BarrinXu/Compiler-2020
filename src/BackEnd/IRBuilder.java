package BackEnd;

import AST.*;
import MIR.Function;
import MIR.IRBlock;
import MIR.IRInst.*;
import MIR.IROperand.*;
import MIR.IRType.*;
import MIR.PhiNode;
import MIR.Root;
import Util.ArrayType;
import Util.classType;
import Util.error.internalError;
import Util.funcDecl;
import Util.globalScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class IRBuilder implements ASTVisitor {

    public globalScope gScope;

    public classType nowClass=null;
    public Function nowFunction=null;
    public IRBlock nowBlock=null;


    public HashSet<IRBlock>reachBlocks=new HashSet<>();
    public HashSet<IRBlock>returnReachBlocks=new HashSet<>();
    public HashMap<IRBlock, PhiNode>phiMap=new HashMap<>();
    public ArrayList<Return> returnInsts =new ArrayList<>();
    public int symbolCnt=0;
    public Root IRRoot;

    public boolean visitingParameters=false;


    public IRBuilder(globalScope gScope, Root IRRoot){
        visitingParameters=false;
        this.gScope=gScope;
        this.IRRoot=IRRoot;
        preSet();
    }

    public void dfs(IRBlock it){
        reachBlocks.add(it);
        for(var node:it.sons)
            if(!reachBlocks.contains(node))
                dfs(node);
    }

    void preset_builtinFunction(String name){
        gScope.getMethodType(name,null,false).func=IRRoot.getFunction("BI_"+name);
    }
    void preSet()
    {
        preset_builtinFunction("print");
        preset_builtinFunction("println");
        preset_builtinFunction("printInt");
        preset_builtinFunction("printlnInt");
        preset_builtinFunction("getString");
        preset_builtinFunction("getInt");
        preset_builtinFunction("toString");

        classType stringType= (classType) gScope.getStringType();
        Function func=new Function("BI_str_length");
        func.returnType= Root.int32_type;
        func.parameters.add(new Parameter(Root.string_type,"str"));
        IRRoot.builtInFunctions.put("BI_str_length",func);
        stringType.localScope.getMethodType("length",null,false).func=func;

        func=new Function("BI_str_substring");
        func.returnType= Root.string_type;
        func.parameters.add(new Parameter(Root.string_type,"str"));
        func.parameters.add(new Parameter(Root.int32_type,"left"));
        func.parameters.add(new Parameter(Root.int32_type,"right"));
        IRRoot.builtInFunctions.put("BI_str_substring",func);
        stringType.localScope.getMethodType("substring",null,false).func=func;

        func=new Function("BI_str_parseInt");
        func.returnType= Root.int32_type;
        func.parameters.add(new Parameter(Root.string_type,"str"));
        IRRoot.builtInFunctions.put("BI_str_parseInt",func);
        stringType.localScope.getMethodType("parseInt",null,false).func=func;

        func=new Function("BI_str_ord");
        func.returnType= Root.int32_type;
        func.parameters.add(new Parameter(Root.string_type,"str"));
        func.parameters.add(new Parameter(Root.int32_type,"pos"));
        IRRoot.builtInFunctions.put("BI_str_ord",func);
        stringType.localScope.getMethodType("ord",null,false).func=func;

    }

    @Override
    public void visit(RootNode it) {

        it.nodeList.forEach(node->{
            if(node instanceof functionNode){
                String name=((functionNode) node).name;
                Function func=new Function(name);
                ((functionNode) node).decl.func=func;
                IRRoot.functions.put(func.name,func);
            }
            else if(node instanceof classNode){
                String name=((classNode) node).name;
                ((classNode) node).methods.forEach(method->{
                    Function func=new Function(name+"_"+method.name);
                    method.decl.func=func;
                    IRRoot.functions.put(func.name,func);
                });
                if(((classNode) node).constructor!=null){
                    Function func=new Function(name+"_"+"CONSTRUCTOR");
                    ((classNode) node).constructor.decl.func=func;
                    IRRoot.functions.put(func.name,func);
                }
            }
        });
        it.nodeList.forEach(node->node.accept(this));
        IRRoot.getFunction("INIT").outBlock.setTerminate(new Return(IRRoot.getFunction("INIT").outBlock,null));
        nowFunction=IRRoot.getFunction("INIT");
        //maybe done
        reachBlocks=new HashSet<>();
        returnReachBlocks=new HashSet<>();
        dfs(nowFunction.inBlock);
        for (var iter = returnInsts.iterator(); iter.hasNext();) {
            var nowInst = iter.next();
            if (returnInstCheck(nowInst.block)) {
                iter.remove();
                nowInst.block.deleteTerminate();
            }
        }
        reachBlocks.removeIf(block -> !block.terminate);
        //maybe done
        nowFunction.blocks.addAll(reachBlocks);
        reachBlocks=null;
        returnReachBlocks=null;
        //to be done

    }

    public String getName(IRBaseOperand operand){
        if(operand instanceof Parameter)
            return ((Parameter) operand).name;
        else if(operand instanceof GlobalRegister)
            return ((GlobalRegister) operand).name;
        else if(operand instanceof Register)
            return ((Register) operand).name;
        else
            return "_CONST";
    }

    public IRBaseOperand loadPointer(IRBlock nowBlock,IRBaseOperand operand){
        if(operand.type instanceof PointerType&&((PointerType) operand.type).needPtrLoad){
            Register dest=new Register(((PointerType) operand.type).dest,"LOADED_"+getName(operand));
            nowBlock.pushInst(new Load(dest,operand,nowBlock));
            if(dest.type instanceof IntType && ((IntType) dest.type).size==8){
                Register trans=new Register(new BoolType(),"zext_"+dest.name);
                nowBlock.pushInst(new Zext(trans,nowBlock,dest));
                return trans;
            }
            return dest;
        }
        else
            return operand;
    }

    @Override
    public void visit(returnStmtNode it) {
        Return returnInst;
        if(it.value==null)
            returnInst=new Return(nowBlock,null);
        else{
            it.value.accept(this);
            IRBaseOperand val;
            if(it.value.operand.type.dim()>nowFunction.returnType.dim()){
                assert it.value.operand.type.dim()==nowFunction.returnType.dim()+1;
                val=loadPointer(nowBlock,it.value.operand);
            }
            else
                val=it.value.operand;
            returnInst=new Return(nowBlock,val);
        }
        nowBlock.setTerminate(returnInst);
        returnInsts.add(returnInst);
    }

    @Override
    public void visit(exprStmtNode it) {
        it.expr.accept(this);
    }

    @Override
    public void visit(ifStmtNode it) {
        IRBlock thenBlock=new IRBlock("if_then"),elseBlock=new IRBlock("if_else"),destBlock=new IRBlock("if_dest");
        if(it.elseStmt==null)
            elseBlock=destBlock;
        it.condition.thenBlock=thenBlock;
        it.condition.elseBlock=elseBlock;
        it.condition.accept(this);

        nowBlock=thenBlock;
        it.thenStmt.accept(this);
        if(!nowBlock.terminate)
            nowBlock.setTerminate(new Jump(nowBlock,destBlock));

        if(it.elseStmt!=null){
            nowBlock=elseBlock;
            it.elseStmt.accept(this);
            if(!nowBlock.terminate)
                nowBlock.setTerminate(new Jump(nowBlock,destBlock));
        }
        if(!thenBlock.terminateWithReturn()||!elseBlock.terminateWithReturn())
            nowBlock=destBlock;
    }

    @Override
    public void visit(assignExprNode it) {
        it.lhs.accept(this);
        assign(it.lhs.operand,it.rhs);
        it.operand=it.rhs.operand;
    }

    public IRBaseOperand loadStringPointer(IRBlock nowBlock, IRBaseOperand operand){
        assert operand.type instanceof PointerType;
        if(((PointerType) operand.type).dest instanceof PointerType){
            Register dest=new Register(((PointerType) operand.type).dest,"loaded_"+getName(operand));
            nowBlock.pushInst(new Load(dest,operand,nowBlock));
            return dest;
        }
        else
            return operand;
    }

    public IRBaseOperand trans_to_int(IRBaseOperand operand){
        if(operand.type instanceof BoolType){
            Register trans=new Register(Root.int32_type,"trans_to_int"+operand.getIdentity());
            nowBlock.pushInst(new Zext(trans,nowBlock,operand));
            return trans;
        }
        else{
            assert operand.type instanceof IntType && ((IntType) operand.type).size==32;
            return operand;
        }
    }

    @Override
    public void visit(binaryExprNode it) {
        Function string_builtin_Call=null;
        Binary.BinaryOpType binaryOpCode=null;
        Cmp.CmpOpType cmpOpCode=null;

        switch (it.opCode){
            case mul:binaryOpCode= Binary.BinaryOpType.mul;break;
            case div:binaryOpCode= Binary.BinaryOpType.sdiv;break;
            case mod:binaryOpCode= Binary.BinaryOpType.srem;break;
            case and:binaryOpCode= Binary.BinaryOpType.and;break;
            case caret:binaryOpCode= Binary.BinaryOpType.xor;break;
            case leftshift:binaryOpCode= Binary.BinaryOpType.shl;break;
            case rightshift:binaryOpCode= Binary.BinaryOpType.ashr;break;
            case or:binaryOpCode= Binary.BinaryOpType.or;break;
            case minus:binaryOpCode= Binary.BinaryOpType.sub;break;
            case plus:{
                if(it.lhs.type.isInt())
                    binaryOpCode= Binary.BinaryOpType.add;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_add");
                break;
            }
            case less:{
                if(it.lhs.type.isInt())
                    cmpOpCode= Cmp.CmpOpType.slt;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_LT");
                break;
            }
            case lessequal:{
                if(it.lhs.type.isInt())
                    cmpOpCode= Cmp.CmpOpType.sle;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_LE");
                break;
            }
            case greater:{
                if(it.lhs.type.isInt())
                    cmpOpCode= Cmp.CmpOpType.sgt;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_GT");
                break;
            }
            case greaterequal:{
                if(it.lhs.type.isInt())
                    cmpOpCode= Cmp.CmpOpType.sge;
                else
                    string_builtin_Call=IRRoot.getFunction("BI_str_GE");
                break;
            }
            case andand:break;
            case oror:break;
            case equal:{
                if(it.lhs.type.isInt()||it.lhs.type.isBool()||it.lhs.type.isNull())
                    cmpOpCode= Cmp.CmpOpType.eq;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_EQ");
                break;
            }
            case notequal:{
                if(it.lhs.type.isInt()||it.lhs.type.isBool()||it.lhs.type.isNull())
                    cmpOpCode= Cmp.CmpOpType.ne;
                else
                    string_builtin_Call= IRRoot.getFunction("BI_str_NEQ");
                break;
            }
        }

        switch (it.opCode){
            case mul:
            case div:
            case mod:
            case and:
            case caret:
            case leftshift:
            case rightshift:
            case or:
            case minus:
            case plus:{
                it.lhs.accept(this);
                it.rhs.accept(this);
                if(binaryOpCode!=null){
                    it.operand=new Register(Root.int32_type,"binary_"+binaryOpCode.toString());
                    nowBlock.pushInst(new Binary((Register) it.operand,nowBlock,binaryOpCode,loadPointer(nowBlock,it.lhs.operand),loadPointer(nowBlock,it.rhs.operand)));
                }
                else{
                    it.operand=new Register(Root.string_type,"binary_string_builtin_plus");
                    ArrayList<IRBaseOperand> parameters=new ArrayList<>();
                    parameters.add(loadStringPointer(nowBlock,it.lhs.operand));
                    parameters.add(loadStringPointer(nowBlock,it.rhs.operand));
                    nowBlock.pushInst(new Call(string_builtin_Call,parameters, (Register) it.operand,nowBlock));
                }
                break;
            }
            case less:
            case lessequal:
            case greater:
            case greaterequal:{
                it.lhs.accept(this);
                it.rhs.accept(this);
                if(cmpOpCode!=null){
                    it.operand=new Register(new BoolType(),"cmp_"+cmpOpCode.toString());
                    nowBlock.pushInst(new Cmp((Register) it.operand,nowBlock,cmpOpCode,loadPointer(nowBlock,it.lhs.operand),loadPointer(nowBlock,it.rhs.operand)));
                }
                else{
                    gen_string_builtin_cmp(it, string_builtin_Call);
                }
                addBranch(it);
                break;
            }
            case equal:
            case notequal:{
                it.lhs.accept(this);
                it.rhs.accept(this);
                it.operand=new Register(new BoolType(),it.opCode.toString());
                if(cmpOpCode!=null) {
                    it.operand=new Register(new BoolType(),"cmp_"+cmpOpCode.toString());
                    nowBlock.pushInst(new Cmp((Register) it.operand, nowBlock, cmpOpCode, trans_to_int(loadPointer(nowBlock, it.lhs.operand)), trans_to_int(loadPointer(nowBlock, it.rhs.operand))));
                }
                else{
                    gen_string_builtin_cmp(it, string_builtin_Call);
                }
                addBranch(it);
                break;
            }
            case andand:{
                if(it.thenBlock!=null){
                    IRBlock pt2Block=new IRBlock("AndAndPt2");
                    it.lhs.thenBlock=pt2Block;
                    it.lhs.elseBlock=it.elseBlock;
                    it.lhs.accept(this);

                    nowBlock=pt2Block;
                    it.rhs.thenBlock=it.thenBlock;
                    it.rhs.elseBlock=it.elseBlock;
                    it.rhs.accept(this);
                }
                else{ // maybe bool a= 1&&1; ???
                    IRBlock pt2Block=new IRBlock("AndAndPt2");
                    IRBlock destBlock=new IRBlock("AndAndDest");
                    PhiNode phiNode =new PhiNode();
                    phiMap.put(destBlock, phiNode);
                    it.operand=new Register(new BoolType(),"AndAnd");
                    it.lhs.thenBlock=pt2Block;
                    it.lhs.elseBlock=destBlock;
                    it.lhs.accept(this);

                    nowBlock=pt2Block;
                    it.rhs.accept(this);
                    phiNode.vals.add(loadPointer(nowBlock,it.rhs.operand));
                    phiNode.blocks.add(nowBlock);
                    nowBlock.setTerminate(new Jump(nowBlock,destBlock));

                    nowBlock=destBlock;
                    nowBlock.add_PhiInst(new Phi((Register) it.operand,nowBlock,new ArrayList<>(phiNode.blocks),new ArrayList<>(phiNode.vals)));
                }
                break;
            }
            case oror:{
                if(it.thenBlock!=null){
                    IRBlock pt2Block=new IRBlock("OrOrPt2");
                    it.lhs.thenBlock=it.thenBlock;
                    it.lhs.elseBlock=pt2Block;
                    it.lhs.accept(this);

                    nowBlock=pt2Block;
                    it.rhs.thenBlock=it.thenBlock;
                    it.rhs.elseBlock=it.elseBlock;
                    it.rhs.accept(this);
                }
                else{ // maybe bool a= 1||1; ???
                    IRBlock pt2Block=new IRBlock("OrOrPt2");
                    IRBlock destBlock=new IRBlock("OrOrDest");

                    PhiNode phiNode =new PhiNode();
                    phiMap.put(destBlock, phiNode);

                    it.operand=new Register(new BoolType(),"OrOr");
                    it.lhs.thenBlock=destBlock;
                    it.lhs.elseBlock=pt2Block;
                    it.lhs.accept(this);
                    phiNode.vals.add(new ConstBool(true));
                    phiNode.blocks.add(nowBlock);

                    nowBlock=pt2Block;
                    it.rhs.accept(this);
                    phiNode.vals.add(loadPointer(nowBlock,it.rhs.operand));
                    phiNode.blocks.add(nowBlock);
                    nowBlock.setTerminate(new Jump(nowBlock,destBlock));

                    nowBlock=destBlock;
                    destBlock.add_PhiInst(new Phi((Register) it.operand,nowBlock,new ArrayList<>(phiNode.blocks),new ArrayList<>(phiNode.vals)));
                }
                break;
            }
        }
    }

    private void gen_string_builtin_cmp(binaryExprNode it, Function string_builtin_Call) {
        it.operand=new Register(new BoolType(),"cmp_string_builtin_"+it.opCode.toString());
        ArrayList<IRBaseOperand> parameters=new ArrayList<>();
        parameters.add(loadStringPointer(nowBlock,it.lhs.operand));
        parameters.add(loadStringPointer(nowBlock,it.rhs.operand));
        nowBlock.pushInst(new Call(string_builtin_Call,parameters, (Register) it.operand,nowBlock));
    }

    @Override
    public void visit(forStmtNode it) {
        IRBlock condBlock=new IRBlock("for_cond"),updBlock=new IRBlock("for_upd"),bodyBlock=new IRBlock("for_body"),destBlock=new IRBlock("for_dest");

        it.destBlock=destBlock;
        if(it.initExpr!=null)
            it.initExpr.accept(this);
        it.updBlock=updBlock;

        if(it.condExpr!=null){
            nowBlock.setTerminate(new Jump(nowBlock,condBlock));
            nowBlock=condBlock;
            it.condExpr.thenBlock=bodyBlock;
            it.condExpr.elseBlock=destBlock;
            it.condExpr.accept(this);
        }else{
            nowBlock.setTerminate(new Jump(nowBlock,bodyBlock));
            condBlock=bodyBlock;
        }

        nowBlock=bodyBlock;
        if(it.loopBody!=null)
            it.loopBody.accept(this);

        if(!nowBlock.terminate)
            nowBlock.setTerminate(new Jump(nowBlock,updBlock));
        nowBlock=updBlock;
        if(it.updExpr!=null)
            it.updExpr.accept(this);
        if(!nowBlock.terminate)
            nowBlock.setTerminate(new Jump(nowBlock,condBlock));
        nowBlock=destBlock;
    }

    public void assign(IRBaseOperand reg, ExprNode expr){
        expr.accept(this);
        IRBaseOperand val=loadPointer(nowBlock, expr.operand);
        IRBaseOperand finalVal;
        assert reg.type instanceof PointerType;
        if(val.type instanceof BoolType){
            if(val instanceof ConstBool)
                finalVal=new ConstInt(((ConstBool) val).val?1:0,8);
            else{
                finalVal=new Register(Root.char_type,"extTo");
                nowBlock.pushInst(new Zext((Register) finalVal,nowBlock,val));
            }
        }
        else
            finalVal=val;
        nowBlock.pushInst(new Store(reg,finalVal,nowBlock));
    }

    @Override
    public void visit(declarationNode it) {
        IRBaseType type=IRRoot.getType(it.entity.type);
        if(it.entity.isGlobal){
            var reg=new GlobalRegister(new PointerType(type,true),it.name);
            it.entity.operand=reg;
            IRRoot.globalRegisters.add(reg);
            if(it.expr!=null){
                nowFunction=IRRoot.getFunction("INIT");
                nowBlock=nowFunction.outBlock;
                assign(it.entity.operand,it.expr);
                nowFunction.outBlock=nowBlock;
                nowFunction=null;
                nowBlock=null;
            }
        }
        else{
            if(visitingParameters){
                Parameter parameter=new Parameter(type,it.name+"_parameter");
                nowFunction.parameters.add(parameter);
                it.entity.operand=new Register(new PointerType(type,true),it.name+"(_address)");
                nowFunction.registers.add((Register) it.entity.operand);
                nowBlock.pushInst(new Store(it.entity.operand,parameter,nowBlock));
            }
            else{
                if(nowFunction!=null){
                    var reg=new Register(new PointerType(type,true),it.name+"_address");
                    if(it.expr!=null)
                        assign(reg,it.expr);
                    nowFunction.registers.add(reg);
                    it.entity.operand=reg;
                }
                else{
                    //???
                    if(type instanceof ClassType)
                        type=new PointerType(type,false);
                    it.entity.operand=new Register(new PointerType(type,true),it.name+"_address");
                }
            }
        }
    }

    @Override
    public void visit(suiteNode it) {
        for(var node:it.statements){
            node.accept(this);
            if(nowBlock.terminate)
                break;
        }
    }

    public boolean returnInstCheck(IRBlock block){
        returnReachBlocks.add(block);
        var backupFas=new ArrayList<>(block.fas);
        backupFas.forEach(fa->{
            if(!returnReachBlocks.contains(fa)){
                if(returnInstCheck(fa))
                    fa.deleteTerminate();
            }
        });
        if(reachBlocks.contains(block))
            return false;
        return block.fas.isEmpty();
    }

    @Override
    public void visit(functionNode it) {
        symbolCnt=0;
        returnInsts.clear();
        nowFunction=it.decl.func;
        nowBlock=nowFunction.inBlock;
        if(it.isMethod)
            nowFunction.setClassPointer(new Parameter(IRRoot.getType(nowClass),"this"));

        nowFunction.returnType=IRRoot.getType(it.decl.type);

        visitingParameters=true;
        it.parameters.forEach(node->node.accept(this));
        visitingParameters=false;

        if(nowFunction.name.equals("main"))
            nowBlock.pushInst(new Call(IRRoot.getFunction("INIT"),new ArrayList<>(),null,nowBlock));
        it.suite.accept(this);

        if(!nowBlock.terminate){
            IRBaseOperand operand;
            if(nowFunction.name.equals("main"))
                operand=new ConstInt(0,32);
            else if(nowFunction.returnType instanceof VoidType)
                operand=null;
            else if(nowFunction.returnType instanceof IntType)
                operand=new ConstInt(0,nowFunction.returnType.size());
            else if(nowFunction.returnType instanceof BoolType)
                operand=new ConstBool(false);
            else if(nowFunction.returnType instanceof PointerType)
                operand=new Null();
            else
                throw new internalError("",it.pos);
            var inst=new Return(nowBlock,operand);
            nowBlock.setTerminate(inst);
            returnInsts.add(inst);
        }

        reachBlocks=new HashSet<>();
        returnReachBlocks=new HashSet<>();
        dfs(nowFunction.inBlock);
        for(var iter=returnInsts.iterator(); iter.hasNext();){
            var nowInst=iter.next();
            if(returnInstCheck(nowInst.block)){
                iter.remove();
                nowInst.block.deleteTerminate();
            }
        }
        reachBlocks.removeIf(block -> !block.terminate);
        nowFunction.blocks.addAll(reachBlocks);
        reachBlocks=null;
        returnReachBlocks=null;

        if(returnInsts.size()>1){
            IRBlock finalReturn=new IRBlock("finalReturn");
            Register returnReg;
            if(returnInsts.get(0).val==null)
                returnReg=null;
            else
                returnReg=new Register(returnInsts.get(0).val.type,"finalReturnReg");
            if(returnReg!=null){
                ArrayList<IRBaseOperand> returnVals=new ArrayList<>();
                ArrayList<IRBlock>returnBlocks=new ArrayList<>();
                returnInsts.forEach(Inst->{
                    Inst.block.deleteTerminate();
                    returnVals.add(Inst.val);
                    returnBlocks.add(Inst.block);
                    Inst.block.setTerminate(new Jump(Inst.block,finalReturn));
                });
                finalReturn.add_PhiInst(new Phi(returnReg,nowBlock,returnBlocks,returnVals));
            }
            else{
                returnInsts.forEach(Inst->{
                    Inst.block.deleteTerminate();
                    Inst.block.setTerminate(new Jump(Inst.block,finalReturn));
                });
            }
            finalReturn.setTerminate(new Return(finalReturn,returnReg));
            nowFunction.outBlock=finalReturn;
            nowFunction.blocks.add(finalReturn);
        }
        else
            nowFunction.outBlock=returnInsts.get(0).block;
        nowFunction.registers.forEach(reg->{
            if(((PointerType)reg.type).dest instanceof PointerType)
                nowFunction.inBlock.pushHeadInst(new Store(reg,new Null(),nowFunction.inBlock));
            else
                nowFunction.inBlock.pushHeadInst(new Store(reg,new ConstInt(65536,((PointerType)reg.type).dest.size()),nowFunction.inBlock));
        });
        returnInsts.clear();
        nowFunction=null;
        nowBlock=null;

    }

    @Override
    public void visit(classNode it) {
        nowClass= (classType) gScope.getTypeFromName(it.name,it.pos);
        symbolCnt=0;
        it.members.forEach(node->node.accept(this));
        it.methods.forEach(node->node.accept(this));
        if(it.constructor!=null)
            it.constructor.accept(this);
    }

    @Override
    public void visit(funcCallNode it) {
        it.call.accept(this);
        funcDecl identity= (funcDecl) it.call.type;
        if(identity.name.equals("size")&&!identity.isClassMethod){
            it.operand=new Register(Root.int32_type,"array_builtin_size");
            IRBaseOperand operand=loadPointer(nowBlock,it.call.operand);
            assert operand.type instanceof PointerType;
            Register oriPtr=new Register(new PointerType(Root.int32_type,false),"oriPtr");
            IRBaseOperand castPtr=new Register(new PointerType(Root.int32_type,false),"castPtr");
            if(((PointerType) operand.type).dest==Root.int32_type&&((PointerType) operand.type).dest.size()==32)
                castPtr=operand;
            else
                nowBlock.pushInst(new BitCast((Register) castPtr,nowBlock,operand));
            nowBlock.pushInst(new GetElementPtr(oriPtr,nowBlock,Root.int32_type,castPtr,new ConstInt(-1,32),null));
            nowBlock.pushInst(new Load((Register) it.operand,oriPtr,nowBlock));
            //???why???
        }
        else{
            if(!it.type.isVoid())
                it.operand=new Register(IRRoot.getType(identity.type),"funcCall_Return");
            else
                it.operand=null;
            ArrayList<IRBaseOperand> parameters=new ArrayList<>();
            if(identity.isClassMethod)
                parameters.add(loadPointer(nowBlock,it.call.operand));
            it.parameters.forEach(node->{
                node.accept(this);
                parameters.add(loadPointer(nowBlock,node.operand));
            });
            nowBlock.pushInst(new Call(identity.func,parameters, (Register) it.operand,nowBlock));
            if(!IRRoot.builtInFunctions.containsKey(identity.func.name))
                nowFunction.callFunc.add(identity.func);
        }

        if(it.operand!=null)
            addBranch(it);
    }

    @Override
    public void visit(memberNode it) {
        it.call.accept(this);
        IRBaseOperand pointToClass=loadPointer(nowBlock,it.call.operand);
        it.operand=new Register(it.entity.operand.type, "this."+it.member);
        nowBlock.pushInst(new GetElementPtr((Register) it.operand,nowBlock,((PointerType)pointToClass.type).dest,pointToClass,new ConstInt(0,32),it.entity.index));
        addBranch(it);
    }

    @Override
    public void visit(newExprNode it) {
        if(it.type instanceof ArrayType){
            it.operand=new Register(IRRoot.getType(it.type),"new_ret");
            arrayMalloc(0,it, (Register) it.operand);
        }
        else{
            Register tmp=new Register(Root.string_type,"malloc_tmp");
            it.operand=new Register(IRRoot.getType(it.type),"new_class_pointer");
            nowBlock.pushInst(new Malloc(tmp,nowBlock,new ConstInt(((classType)it.type).memSize,32)));
            nowBlock.pushInst(new BitCast((Register) it.operand,nowBlock,tmp));
            if(((classType)it.type).localScope.constructor!=null){
                ArrayList<IRBaseOperand>parameters=new ArrayList<>();
                parameters.add(it.operand);
                nowBlock.pushInst(new Call(((classType)it.type).localScope.constructor.func,parameters,null,nowBlock));
            }
        }
    }

    @Override
    public void visit(prefixExprNode it) {
        it.operand=new Register(it.opcode== prefixExprNode.prefixOpType.not?Root.bool_type:Root.int32_type,"prefix_"+it.opcode.toString());
        if(it.opcode== prefixExprNode.prefixOpType.not) {
            if(it.thenBlock!=null){
                it.expr.thenBlock=it.elseBlock;
                it.expr.elseBlock=it.thenBlock;
                it.expr.accept(this);
                return;
            }
        }
        it.expr.accept(this);
        IRBaseOperand val=loadPointer(nowBlock,it.expr.operand);
        switch (it.opcode){
            case selfplus:
            case selfminus:{
                nowBlock.pushInst(new Binary((Register) it.operand,nowBlock, it.opcode== prefixExprNode.prefixOpType.selfplus?Binary.BinaryOpType.add: Binary.BinaryOpType.sub,val,new ConstInt(1,32)));
                if(it.expr.isAssignable)
                    nowBlock.pushInst(new Store(it.expr.operand,it.operand,nowBlock));
                break;
            }
            case plus:{
                it.operand=it.expr.operand;
                break;
            }
            case minus:{
                nowBlock.pushInst(new Binary((Register) it.operand,nowBlock, Binary.BinaryOpType.sub,new ConstInt(0,32),val));
                break;
            }
            case tilde:{
                nowBlock.pushInst(new Binary((Register) it.operand,nowBlock, Binary.BinaryOpType.xor,val,new ConstInt(-1,32)));
                break;
            }
            case not:{
                nowBlock.pushInst(new Binary((Register) it.operand,nowBlock, Binary.BinaryOpType.xor,val,new ConstBool(true)));
                break;
            }
        }
        addBranch(it);
    }

    @Override
    public void visit(suffixExprNode it) {
        it.expr.accept(this);
        Register val_tmp=new Register(Root.int32_type,"suffix_val_tmp");
        it.operand=loadPointer(nowBlock,it.expr.operand);
        if(it.opcode== suffixExprNode.suffixOpType.selfplus)
            nowBlock.pushInst(new Binary(val_tmp,nowBlock, Binary.BinaryOpType.add,it.operand,new ConstInt(1,32)));
        else
            nowBlock.pushInst(new Binary(val_tmp,nowBlock, Binary.BinaryOpType.sub,it.operand,new ConstInt(1,32)));
        nowBlock.pushInst(new Store(it.expr.operand,val_tmp,nowBlock));
    }

    @Override
    public void visit(declarationBlockNode it) {
        it.decls.forEach(node->node.accept(this));
    }

    @Override
    public void visit(breakNode it) {
        assert it.dest instanceof forStmtNode;
        nowBlock.setTerminate(new Jump(nowBlock,((forStmtNode)it.dest).destBlock));
    }

    @Override
    public void visit(continueNode it) {
        assert it.dest instanceof forStmtNode;
        nowBlock.setTerminate(new Jump(nowBlock,((forStmtNode)it.dest).updBlock));
    }

    public void addBranch(ExprNode it){
        if(it.thenBlock!=null){
            IRBaseOperand dest=loadPointer(nowBlock,it.operand);
            assert dest.type instanceof BoolType;
            nowBlock.setTerminate(new Branch(nowBlock,dest,it.thenBlock,it.elseBlock));
            if(phiMap.containsKey(it.thenBlock)){
                PhiNode phiNode=phiMap.get(it.thenBlock);
                phiNode.vals.add(new ConstBool(true));
                phiNode.blocks.add(nowBlock);
            }
            if(phiMap.containsKey(it.elseBlock)){
                PhiNode phiNode=phiMap.get(it.elseBlock);
                phiNode.vals.add(new ConstBool(false));
                phiNode.blocks.add(nowBlock);
            }
        }
    }

    @Override
    public void visit(indexExprNode it) {
        it.lhs.accept(this);
        it.rhs.accept(this);
        IRBaseOperand pointer=loadPointer(nowBlock,it.lhs.operand),arrayOffset=loadPointer(nowBlock,it.rhs.operand);
        it.operand=new Register(new PointerType(IRRoot.getType(it.type),true),"Pointer_Array");
        nowBlock.pushInst(new GetElementPtr((Register) it.operand,nowBlock,((PointerType)pointer.type).dest,pointer,arrayOffset,null));
        addBranch(it);
    }

    @Override
    public void visit(identifierNode it) {
        if(it.entity.isMember){
            Parameter class_pointer=nowFunction.classPointer;
            it.operand=new Register(it.entity.operand.type,"this."+it.name+"_address");
            nowBlock.pushInst(new GetElementPtr((Register) it.operand,nowBlock,((PointerType)class_pointer.type).dest,class_pointer,new ConstInt(0,32),it.entity.index));
        }
        else{
            it.operand=it.entity.operand;
        }
        addBranch(it);
    }

    @Override
    public void visit(thisNode it) {
        it.operand=nowFunction.classPointer;
    }

    @Override
    public void visit(typeNode it) {

    }

    @Override
    public void visit(funcExpr it) {
        if(((funcDecl)it.type).isClassMethod)
            it.operand=nowFunction.classPointer;
    }

    @Override
    public void visit(methodExpr it) {
        it.call.accept(this);
        it.operand=it.call.operand;
    }

    @Override
    public void visit(intLiteral it) {
        it.operand=new ConstInt(it.value,32);
    }

    @Override
    public void visit(stringLiteral it) {
        String name;
        String val =it.value.substring(1,it.value.length()-1);
        val = val.replace("\\n", "\n");//can delete why
        val = val.replace("\\t", "\t");//can delete why
        val = val.replace("\\\"", "\"");
        val = val.replace("\\\\", "\\");
        val += "\0";
        ConstString operand=IRRoot.stringVals.getOrDefault(val,null);
        if(operand==null){
            name=nowFunction.name+"."+(symbolCnt++);
            IRRoot.addConstString(name,val);
            operand=IRRoot.constStrings.get(name);
        }
        else
            name=operand.name;
        it.operand=new Register(Root.string_type,"loaded_"+name);
        nowBlock.pushInst(new GetElementPtr((Register) it.operand,nowBlock,new MIR.IRType.ArrayType(val.length(),Root.char_type),operand,new ConstInt(0,32),new ConstInt(0,32)));
    }

    @Override
    public void visit(nullLiteral it) {
        it.operand=new Null();
    }

    @Override
    public void visit(boolLiteral it) {
        it.operand=new ConstBool(it.value);
        addBranch(it);
    }

    public void arrayMalloc(int nowDim, newExprNode it, Register ret){
        Register arrayPtr;
        if(nowDim ==0)
            arrayPtr=ret;
        else
            arrayPtr=new Register(((PointerType)ret.type).dest,"mallocArrayPtr");
        if(nowDim ==it.exprs.size())
            return;
        it.exprs.get(nowDim).accept(this);
        IRBaseType dest=((PointerType)arrayPtr.type).dest;

        IRBaseOperand nowIndex=loadPointer(nowBlock,it.exprs.get(nowDim).operand);
        ConstInt typeByte=new ConstInt(((PointerType) arrayPtr.type).dest.size()/8,32);
        Register oriWidth=new Register(Root.int32_type,"oriWidth");
        Register realWidth=new Register(Root.int32_type,"realWidth");

        Register allocPtr=new Register(Root.string_type,"allocPtr");
        Register allocBitCast=new Register(new PointerType(Root.int32_type,false),"allocBitCast");
        Register allocOffset=new Register(new PointerType(Root.int32_type,false),"allocOffset");

        nowBlock.pushInst(new Binary(oriWidth,nowBlock, Binary.BinaryOpType.mul,nowIndex,typeByte));
        nowBlock.pushInst(new Binary(realWidth,nowBlock, Binary.BinaryOpType.add,oriWidth,new ConstInt(4,32)));
        nowBlock.pushInst(new Malloc(allocPtr,nowBlock,realWidth));

        nowBlock.pushInst(new BitCast(allocBitCast,nowBlock,allocPtr));
        nowBlock.pushInst(new Store(allocBitCast,nowIndex,nowBlock));

        if(dest instanceof IntType && dest.size()==32){
            nowBlock.pushInst(new GetElementPtr(arrayPtr,nowBlock,Root.int32_type,allocBitCast,new ConstInt(1,32),null));
        }
        else{
            nowBlock.pushInst(new GetElementPtr(allocOffset,nowBlock,Root.int32_type,allocBitCast,new ConstInt(1,32),null));
            nowBlock.pushInst(new BitCast(arrayPtr,nowBlock,allocOffset));
        }

        if(nowDim !=0)
            nowBlock.pushInst(new Store(ret,arrayPtr,nowBlock));
        if(nowDim<it.exprs.size()-1){
            IRBlock updBlock=new IRBlock("ArrayUpdBlk");
            IRBlock bodyBlock=new IRBlock("ArrayBodyBlk");
            IRBlock destBlock=new IRBlock("ArrayDestBlk");
            assert dest instanceof PointerType;
            Register oriPtr=new Register(new PointerType(Root.int32_type,false),"oriPtr");
            Register castPtr=new Register(arrayPtr.type, "castPtr");
            Register indexVisitCnt=new Register(Root.int32_type,"indexVisitCnt");
            Register indexNewCnt=new Register(Root.int32_type,"indexNewCnt");
            Register mallocBranchResult=new Register(new BoolType(),"mallocBranchResult");

            ArrayList<IRBaseOperand>vals=new ArrayList<>();
            ArrayList<IRBlock>blocks=new ArrayList<>();
            vals.add(new ConstInt(0,32));
            blocks.add(nowBlock);
            nowBlock.setTerminate(new Jump(nowBlock,updBlock));

            nowBlock=updBlock;
            nowBlock.pushInst(new Binary(indexNewCnt,nowBlock, Binary.BinaryOpType.add,indexVisitCnt,new ConstInt(1,32)));
            nowBlock.pushInst(new Cmp(mallocBranchResult,nowBlock, Cmp.CmpOpType.sle,indexVisitCnt,nowIndex));
            nowBlock.setTerminate(new Branch(nowBlock,mallocBranchResult,bodyBlock,destBlock));
            //Why

            nowBlock=bodyBlock;
            nowBlock.pushInst(new GetElementPtr(oriPtr,nowBlock,Root.int32_type,allocBitCast,indexVisitCnt,null));
            nowBlock.pushInst(new BitCast(castPtr,nowBlock,oriPtr));

            arrayMalloc(nowDim+1,it,castPtr);

            nowBlock.setTerminate(new Jump(nowBlock,updBlock));
            vals.add(indexNewCnt);
            blocks.add(nowBlock);

            nowBlock=updBlock;
            nowBlock.add_PhiInst(new Phi(indexVisitCnt,nowBlock,blocks,vals));

            nowBlock=destBlock;
        }
    }
}
