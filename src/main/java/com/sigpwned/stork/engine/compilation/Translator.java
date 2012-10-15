package com.sigpwned.stork.engine.compilation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.ParameterAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.CastExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.InvokeExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.LambdaExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.VarExprAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.DeclareStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.EvalStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.FunctionStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.ReturnStmtAST;
import com.sigpwned.stork.engine.compilation.ast.type.FunctionTypeExpr;
import com.sigpwned.stork.engine.compilation.ast.type.SymbolTypeExpr;
import com.sigpwned.stork.engine.compilation.type.FunctionType;
import com.sigpwned.stork.engine.compilation.type.NumericType;
import com.sigpwned.stork.engine.compilation.type.numeric.FloatType;
import com.sigpwned.stork.engine.compilation.type.numeric.IntType;
import com.sigpwned.stork.engine.compilation.x.ArgumentMismatchException;
import com.sigpwned.stork.engine.compilation.x.DeadCodeStorkException;
import com.sigpwned.stork.engine.compilation.x.DuplicateVariableException;
import com.sigpwned.stork.engine.compilation.x.FunctionMayNotReturnException;
import com.sigpwned.stork.engine.compilation.x.IncompatibleTypesException;
import com.sigpwned.stork.engine.compilation.x.InternalCompilationStorkException;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.engine.compilation.x.NotAFunctionException;
import com.sigpwned.stork.engine.compilation.x.NotAnLValueException;
import com.sigpwned.stork.engine.compilation.x.PrecisionLossException;
import com.sigpwned.stork.engine.compilation.x.ReturnNotInFunctionException;
import com.sigpwned.stork.engine.compilation.x.UndefinedTypeException;
import com.sigpwned.stork.engine.compilation.x.UndefinedVariableException;
import com.sigpwned.stork.engine.compilation.x.UninitializedVariableException;
import com.sigpwned.stork.engine.runtime.Block;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Stmt;
import com.sigpwned.stork.engine.runtime.expr.BinaryOperatorExpr;
import com.sigpwned.stork.engine.runtime.expr.FloatExpr;
import com.sigpwned.stork.engine.runtime.expr.FloatToIntExpr;
import com.sigpwned.stork.engine.runtime.expr.IntExpr;
import com.sigpwned.stork.engine.runtime.expr.IntToFloatExpr;
import com.sigpwned.stork.engine.runtime.expr.InvokeExpr;
import com.sigpwned.stork.engine.runtime.expr.LambdaExpr;
import com.sigpwned.stork.engine.runtime.expr.UnaryOperatorExpr;
import com.sigpwned.stork.engine.runtime.expr.VarAssignExpr;
import com.sigpwned.stork.engine.runtime.expr.VarExpr;
import com.sigpwned.stork.engine.runtime.stmt.DeclareStmt;
import com.sigpwned.stork.engine.runtime.stmt.EvalStmt;
import com.sigpwned.stork.engine.runtime.stmt.FunctionStmt;
import com.sigpwned.stork.engine.runtime.stmt.ReturnStmt;
import com.sigpwned.stork.x.StorkException;

public class Translator {
	private Gamma globe;
	
	public Translator() {
		this.globe = new Gamma(null, null);
		this.globe.addType("Int", Type.INT);
		this.globe.addType("Float", Type.FLOAT);
	}
	
	public Gamma getGlobe() {
		return globe;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// STATEMENT TRANSLATION //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public Stmt translate(Gamma gamma, EvalStmtAST ast) {
		return new EvalStmt(ast.getExpr().translate(gamma, this));
	}
	
	public Stmt translate(Gamma gamma, DeclareStmtAST ast) {
		String name=ast.getName();
		if(gamma.listSlots().contains(name))
			throw new DuplicateVariableException(name);
		
		Type type=ast.getType().eval(gamma, this);
		gamma.addSlot(name, type);
		
		Expr init=ast.getInit()!=null ? ast.getInit().translate(gamma, this) : null;
		if(init != null)
			gamma.getSlot(name).setFlag(Gamma.Slot.Flag.INITIALIZED);
		
		return new DeclareStmt(ast.getName(), init);
	}
	
	public Stmt translate(Gamma gamma, FunctionStmtAST ast) {
		String name=ast.getName();

		FunctionType type;
		try {
			type = (FunctionType) gamma.getSlot(name).getType();
		}
		catch(ClassCastException e) {
			throw new InternalCompilationStorkException("Not a FunctionType: "+gamma.getSlot(name).getType());
		}
		
		String[] parameterNames=new String[ast.numParameters()];
		for(int i=0;i<ast.numParameters();i++)
			parameterNames[i] = ast.getParameter(i).getName();
		
		Gamma inner=new Gamma(gamma, type);
		for(int i=0;i<ast.numParameters();i++) {
			ParameterAST parameter=ast.getParameter(i);
			inner.addSlot(parameter.getName(), parameter.getType().eval(gamma, this)).setFlag(Gamma.Slot.Flag.INITIALIZED);
		}
		
		for(StmtAST stmt : ast.getBody().getBody())
			stmt.defineFunctions(inner, this);
		
		List<Stmt> body=new ArrayList<Stmt>();
		for(int i=0;i<ast.getBody().getBody().size();i++) {
			StmtAST stmt=ast.getBody().getBody().get(i);
			if(inner.hasFlag(Gamma.Flag.RETURNED))
				throw new DeadCodeStorkException(stmt);
			body.add(stmt.translate(inner, this));
		}
		if(!inner.hasFlag(Gamma.Flag.RETURNED))
			throw new FunctionMayNotReturnException(name);
		
		return new FunctionStmt(name, parameterNames, new Block(body.toArray(new Stmt[]{})));
	}
	
	public void defineFunctions(Gamma gamma, FunctionStmtAST ast) {
		Type resultType=ast.getResultType().eval(gamma, this);
		
		Type[] parameterTypes=new Type[ast.numParameters()];
		for(int i=0;i<ast.numParameters();i++) {
			ParameterAST parameter=ast.getParameter(i);
			parameterTypes[i] = parameter.getType().eval(gamma, this);
		}
		
		Type type=new FunctionType(resultType, parameterTypes);

		gamma.addSlot(ast.getName(), type).setFlag(Gamma.Slot.Flag.INITIALIZED);
	}
	
	public Stmt translate(Gamma gamma, ReturnStmtAST ast) {
		if(gamma.getFunctionType() == null)
			throw new ReturnNotInFunctionException(ast);
		
		Expr expr=ast.getExpr().translate(gamma, this);
		Type type=ast.getExpr().typeOf(gamma, this);
		expr = coerce(expr, type, gamma.getFunctionType().getResultType(), false);
		
		gamma.addFlag(Gamma.Flag.RETURNED);
		
		return new ReturnStmt(expr);
	}
	
	// Miscellaneous StmtAST Functions ////////////////////////////////////////
	public void defineFunctions(Gamma gamma, StmtAST ast) {
		// Most StmtAST objects don't define a function, so just define a
		// catch-all defineFunctions() method that will serve most StmtAST
		// types. Any StmtAST classes that actually DO define a function can
		// then implement a more specific method.
	}
	
	///////////////////////////////////////////////////////////////////////////
	// EXPRESSION TRANSLATION /////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	// BinaryOperatorExprAST //////////////////////////////////////////////////
	@SuppressWarnings("serial")
	private static final Map<Type,BinaryOperatorExpr.Operator> binplusRuntimeOperators=new HashMap<Type,BinaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, BinaryOperatorExpr.Operator.FADD);
		put(Type.INT, BinaryOperatorExpr.Operator.IADD);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<Type,BinaryOperatorExpr.Operator> binminusRuntimeOperators=new HashMap<Type,BinaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, BinaryOperatorExpr.Operator.FSUB);
		put(Type.INT, BinaryOperatorExpr.Operator.ISUB);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<Type,BinaryOperatorExpr.Operator> bintimesRuntimeOperators=new HashMap<Type,BinaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, BinaryOperatorExpr.Operator.FMUL);
		put(Type.INT, BinaryOperatorExpr.Operator.IMUL);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<Type,BinaryOperatorExpr.Operator> bindivideRuntimeOperators=new HashMap<Type,BinaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, BinaryOperatorExpr.Operator.FDIV);
		put(Type.INT, BinaryOperatorExpr.Operator.IDIV);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<Type,BinaryOperatorExpr.Operator> binmodulusRuntimeOperators=new HashMap<Type,BinaryOperatorExpr.Operator>() {{
		put(Type.INT, BinaryOperatorExpr.Operator.MOD);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<BinaryOperatorExprAST.Operator,Map<Type,BinaryOperatorExpr.Operator>> binaryOperators=new EnumMap<BinaryOperatorExprAST.Operator,Map<Type,BinaryOperatorExpr.Operator>>(BinaryOperatorExprAST.Operator.class) {{
		put(BinaryOperatorExprAST.Operator.PLUS, binplusRuntimeOperators);
		put(BinaryOperatorExprAST.Operator.MINUS, binminusRuntimeOperators);
		put(BinaryOperatorExprAST.Operator.TIMES, bintimesRuntimeOperators);
		put(BinaryOperatorExprAST.Operator.DIVIDE, bindivideRuntimeOperators);
		put(BinaryOperatorExprAST.Operator.MOD, binmodulusRuntimeOperators);
	}};
	
	public Expr translate(Gamma gamma, BinaryOperatorExprAST expr) {
		Expr result;
		
		if(expr.getOperator() == BinaryOperatorExprAST.Operator.EQ)
			result = expr.getLeft().assign(gamma, this, expr.getRight());
		else {
			Type ltype=expr.getLeft().typeOf(gamma, this);
			Type rtype=expr.getRight().typeOf(gamma, this);
			Type resultType=computeBinaryOperatorResultType(ltype, rtype);
			
			Map<Type,BinaryOperatorExpr.Operator> runtimeOperators=binaryOperators.get(expr.getOperator());
			if(runtimeOperators == null)
				throw new InternalCompilationStorkException("Unrecognized BinaryOperatorExpr.Operator: "+expr.getOperator());
			
			if(runtimeOperators.containsKey(resultType)) {
				BinaryOperatorExpr.Operator operator=runtimeOperators.get(resultType);
				if(operator != null) {
					result = new BinaryOperatorExpr(
						operator,
						coerce(expr.getLeft().translate(gamma, this), ltype, resultType, false),
						coerce(expr.getRight().translate(gamma, this), rtype, resultType, false));
				}
				else
					throw new InternalCompilationStorkException("Implicit binary operator not allowed: "+expr.getOperator());
			}
			else
				throw new NoSuchOperatorException(expr.getOperator().getText(), ltype, rtype);
		}

		return result;
	}
	
	public Type typeOf(Gamma gamma, BinaryOperatorExprAST expr) {
		Type ltype=expr.getLeft().typeOf(gamma, this);
		Type rtype=expr.getRight().typeOf(gamma, this);
		return computeBinaryOperatorResultType(ltype, rtype);		
	}
	
	protected static Type computeBinaryOperatorResultType(Type left, Type right) {
		Type result;
		if(left.equals(right))
			result = left;
		else
		if(left instanceof NumericType && right instanceof NumericType) {
			NumericType nleft=(NumericType) left;
			NumericType nright=(NumericType) right;
			if(nleft.getPrecision() > nright.getPrecision())
				result = nleft;
			else
				result = right;
		}
		else
			throw new StorkException("Unrecognized type: "+left);
		return result;
	}
	
	// UnaryOperatorExprAST ///////////////////////////////////////////////////
	@SuppressWarnings("serial")
	private static final Map<Type,UnaryOperatorExpr.Operator> unplusRuntimeOperators=new HashMap<Type,UnaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, null);
		put(Type.INT, null);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<Type,UnaryOperatorExpr.Operator> unminusRuntimeOperators=new HashMap<Type,UnaryOperatorExpr.Operator>() {{
		put(Type.FLOAT, UnaryOperatorExpr.Operator.FNEG);
		put(Type.INT, UnaryOperatorExpr.Operator.INEG);
	}};
	
	@SuppressWarnings("serial")
	private static final Map<UnaryOperatorExprAST.Operator,Map<Type,UnaryOperatorExpr.Operator>> unaryOperators=new EnumMap<UnaryOperatorExprAST.Operator,Map<Type,UnaryOperatorExpr.Operator>>(UnaryOperatorExprAST.Operator.class) {{
		put(UnaryOperatorExprAST.Operator.POSITIVE, unplusRuntimeOperators);
		put(UnaryOperatorExprAST.Operator.NEGATIVE, unminusRuntimeOperators);
	}};
	
	public Expr translate(Gamma gamma, UnaryOperatorExprAST expr) {
		Expr result;
		
		Type type=expr.typeOf(gamma, this);
		
		Map<Type,UnaryOperatorExpr.Operator> runtimeOperators=unaryOperators.get(expr.getOperator());
		if(runtimeOperators == null)
			throw new InternalCompilationStorkException("Unrecognized UnaryOperatorExpr.Operator: "+expr.getOperator());
		
		if(runtimeOperators.containsKey(type)) {
			UnaryOperatorExpr.Operator operator=runtimeOperators.get(type);
			if(operator != null) {
				result = new UnaryOperatorExpr(
					operator,
					expr.getChild().translate(gamma, this));
			}
			else {
				// If there is an "empty slot" for our runtime operator, that means that
				// this is a valid operation, but has no side-effect. (For example, 
				// unary plus has no effect.) Just return the given expression.
				result = expr.getChild().translate(gamma, this);
			}
		}
		else
			throw new NoSuchOperatorException(expr.getOperator().getText(), type);

		return result;
	}
	
	public Type typeOf(Gamma gamma, UnaryOperatorExprAST expr) {
		return expr.getChild().typeOf(gamma, this);
	}
	
	// IntExprAST /////////////////////////////////////////////////////////////
	public IntExpr translate(Gamma gamma, IntExprAST expr) {
		return new IntExpr(expr.getValue());
	}
	
	public IntType typeOf(Gamma gamma, IntExprAST expr) {
		return Type.INT;
	}
	
	// FloatExprAST ///////////////////////////////////////////////////////////
	public FloatExpr translate(Gamma gamma, FloatExprAST expr) {
		return new FloatExpr(expr.getValue());
	}
	
	public FloatType typeOf(Gamma gamma, FloatExprAST expr) {
		return Type.FLOAT;
	}
	
	// VarExprAST /////////////////////////////////////////////////////////////
	public Expr translate(Gamma gamma, VarExprAST expr) {
		Expr result;
		
		if(!gamma.listSlots().contains(expr.getName()))
			throw new UndefinedVariableException(expr.getName());
		else
		if(!gamma.getSlot(expr.getName()).hasFlag(Gamma.Slot.Flag.INITIALIZED))
			throw new UninitializedVariableException(expr.getName());
		else
			result = new VarExpr(expr.getName());
		
		return result;
	}
	
	public Type typeOf(Gamma gamma, VarExprAST expr) {
		Type result;
		if(gamma.listSlots().contains(expr.getName()))
			result = gamma.getSlot(expr.getName()).getType();
		else
			throw new UndefinedVariableException(expr.getName());
		return result;
	}
	
	public Expr assign(Gamma gamma, VarExprAST left, ExprAST right) {
		Expr result;
		
		Expr rvalue=right.translate(gamma, this);
		
		if(left instanceof VarExprAST) {
			VarExprAST lvalue=(VarExprAST) left;
			if(!gamma.listSlots().contains(lvalue.getName()))
				throw new UndefinedVariableException(lvalue.getName());
			else {
				Type ltype=gamma.getSlot(lvalue.getName()).getType();
				result = new VarAssignExpr(lvalue.getName(), coerce(rvalue, right.typeOf(gamma, this), ltype, false));
				gamma.getSlot(lvalue.getName()).setFlag(Gamma.Slot.Flag.INITIALIZED);
			}
		}
		else
			throw new NotAnLValueException(left);
		
		return result;
	}
	
	// CastExprAST ////////////////////////////////////////////////////////////
	public Expr translate(Gamma gamma, CastExprAST expr) {
		Type castType=expr.getType().eval(gamma, this);

		Type valueType=expr.getExpr().typeOf(gamma, this);
		
		Expr value=expr.getExpr().translate(gamma, this);
		
		return coerce(value, valueType, castType, true);
	}
	
	public Type typeOf(Gamma gamma, CastExprAST expr) {
		return expr.getType().eval(gamma, this);
	}
	
	// LambdaExprAST //////////////////////////////////////////////////////////
	public Expr translate(Gamma gamma, LambdaExprAST expr) {
		typeOf(gamma, expr);

		Gamma inner=new Gamma(gamma, null);
		String[] parameterNames=new String[expr.numParameters()];
		for(int i=0;i<expr.numParameters();i++) {
			ParameterAST parameter=expr.getParameter(i);
			parameterNames[i] = parameter.getName();
			inner.addSlot(parameter.getName(), parameter.getType().eval(gamma, this)).setFlag(Gamma.Slot.Flag.INITIALIZED);
		}
		
		Block body=new Block(new Stmt[] {
			new ReturnStmt(expr.getBody().translate(inner, this))
		});
		
		return new LambdaExpr(parameterNames, body);
	}
	
	public FunctionType typeOf(Gamma gamma, LambdaExprAST expr) {
		Gamma inner=new Gamma(gamma, null);
		
		Type[] parameterTypes=new Type[expr.numParameters()];
		for(int i=0;i<expr.numParameters();i++) {
			ParameterAST parameter=expr.getParameter(i);
			Type parameterType=expr.getParameter(i).getType().eval(gamma, this);
			parameterTypes[i] = parameterType;
			inner.addSlot(parameter.getName(), parameterType).setFlag(Gamma.Slot.Flag.INITIALIZED);
		}
		
		Type resultType=expr.getBody().typeOf(inner, this);
		
		return new FunctionType(resultType, parameterTypes);
	}
	
	// InvokeExprAST //////////////////////////////////////////////////////////
	public Expr translate(Gamma gamma, InvokeExprAST expr) {
		Expr function=expr.getFunction().translate(gamma, this);

		FunctionType type=functionType(gamma, expr.getFunction());
		if(type.numParameterTypes() != expr.numArguments())
			throw new ArgumentMismatchException(type, expr);
		
		List<Expr> arguments=new ArrayList<Expr>();
		for(int i=0;i<type.numParameterTypes();i++) {
			Type argumentType=expr.getArgument(i).typeOf(gamma, this);
			Expr argument=expr.getArgument(i).translate(gamma, this);
			Type parameterType=type.getParameterType(i);
			argument = coerce(argument, argumentType, parameterType, false);
			arguments.add(argument);
		}
		
		return new InvokeExpr(function, arguments.toArray(new Expr[]{}));
	}
	
	public Type typeOf(Gamma gamma, InvokeExprAST expr) {
		return functionType(gamma, expr.getFunction());
	}
	
	private FunctionType functionType(Gamma gamma, ExprAST expr) {
		FunctionType result;
		try {
			result = (FunctionType) expr.typeOf(gamma, this);
		}
		catch(ClassCastException e) {
			throw new NotAFunctionException(expr);
		}
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// TYPE EVALUATION ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public Type eval(Gamma gamma, SymbolTypeExpr texpr) {
		Type result;
		if(gamma.hasType(texpr.getName()))
			result = gamma.getType(texpr.getName());
		else
			throw new UndefinedTypeException(texpr.getName());
		return result;
	}
	
	public Type eval(Gamma gamma, FunctionTypeExpr texpr) {
		Type[] parameterTypes=new Type[texpr.getParameterTypes().size()];
		for(int i=0;i<texpr.getParameterTypes().size();i++)
			parameterTypes[i] = texpr.getParameterTypes().get(i).eval(gamma, this);
		Type resultType=texpr.getResultType().eval(gamma, this);
		return new FunctionType(resultType, parameterTypes);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// UTILITY ////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////	
	protected static Expr coerce(Expr expr, Type type, Type target, boolean explicit) {
		Expr result;

		if(type.equals(target))
			result = expr;
		else
		if(type instanceof NumericType && target instanceof NumericType) {
			NumericType ntype=(NumericType) type;
			NumericType ntarget=(NumericType) target;
			if(ntype.getPrecision()<ntarget.getPrecision() || explicit) {
				if(ntype.equals(Type.INT) && ntarget.equals(Type.FLOAT))
					result = new IntToFloatExpr(expr);
				else
				if(ntype.equals(Type.FLOAT) && ntarget.equals(Type.INT))
					result = new FloatToIntExpr(expr);
				else
					throw new InternalCompilationStorkException("Asked to coerce unrecognized numeric type(s) `"+type.getText()+"' to `"+target.getText()+"'"); 
			}
			else
				throw new PrecisionLossException(type, target);
		}
		else
			throw new IncompatibleTypesException(type, target);
		
		return result;
	}

	/**
	 * Default assignment method for non l-value
	 * {@link com.sigpwned.stork.engine.compilation.ast.ExprAST}.
	 */
	public Expr assign(Gamma gamma, ExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
}
