package com.sigpwned.stork.engine.compilation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.CastExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.VarExprAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.DeclareStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.EvalStmtAST;
import com.sigpwned.stork.engine.compilation.type.NumericType;
import com.sigpwned.stork.engine.compilation.x.DuplicateVariableException;
import com.sigpwned.stork.engine.compilation.x.IncompatibleTypesException;
import com.sigpwned.stork.engine.compilation.x.InternalCompilationStorkException;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.engine.compilation.x.NotAnLValueException;
import com.sigpwned.stork.engine.compilation.x.PrecisionLossException;
import com.sigpwned.stork.engine.compilation.x.UndefinedTypeException;
import com.sigpwned.stork.engine.compilation.x.UndefinedVariableException;
import com.sigpwned.stork.engine.compilation.x.UninitializedVariableException;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Stmt;
import com.sigpwned.stork.engine.runtime.expr.BinaryOperatorExpr;
import com.sigpwned.stork.engine.runtime.expr.FloatExpr;
import com.sigpwned.stork.engine.runtime.expr.FloatToIntExpr;
import com.sigpwned.stork.engine.runtime.expr.IntExpr;
import com.sigpwned.stork.engine.runtime.expr.IntToFloatExpr;
import com.sigpwned.stork.engine.runtime.expr.UnaryOperatorExpr;
import com.sigpwned.stork.engine.runtime.expr.VarAssignExpr;
import com.sigpwned.stork.engine.runtime.expr.VarExpr;
import com.sigpwned.stork.engine.runtime.stmt.DeclareStmt;
import com.sigpwned.stork.engine.runtime.stmt.EvalStmt;
import com.sigpwned.stork.x.StorkException;

public class Translator {
	private Gamma globe;
	
	public Translator() {
		this.globe = new Gamma();
		this.globe.addType("Int", Type.INT);
		this.globe.addType("Float", Type.FLOAT);
	}
	
	protected Gamma getGlobe() {
		return globe;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// STATEMENT TRANSLATION //////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public Stmt translate(EvalStmtAST ast) {
		return new EvalStmt(ast.getExpr().translate(this));
	}
	
	public Stmt translate(DeclareStmtAST ast) {
		String name=ast.getName();
		if(getGlobe().listSlots().contains(name))
			throw new DuplicateVariableException(name);
		
		Type type=ast.getType().eval(this);
		getGlobe().addSlot(name, type);
		
		Expr init=ast.getInit()!=null ? ast.getInit().translate(this) : null;
		if(init != null)
			getGlobe().getSlot(name).setFlag(Gamma.Slot.Flag.INITIALIZED);
		
		return new DeclareStmt(ast.getName(), init);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// EXPRESSION TRANSLATION /////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	// BINARY OPERATORS ///////////////////////////////////////////////////////
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
	
	public Expr translate(BinaryOperatorExprAST expr) {
		Expr result;
		
		if(expr.getOperator() == BinaryOperatorExprAST.Operator.EQ)
			result = expr.getLeft().assign(this, expr.getRight());
		else {
			Type ltype=expr.getLeft().typeOf(this);
			Type rtype=expr.getRight().typeOf(this);
			Type resultType=computeBinaryOperatorResultType(ltype, rtype);
			
			Map<Type,BinaryOperatorExpr.Operator> runtimeOperators=binaryOperators.get(expr.getOperator());
			if(runtimeOperators == null)
				throw new InternalCompilationStorkException("Unrecognized BinaryOperatorExpr.Operator: "+expr.getOperator());
			
			if(runtimeOperators.containsKey(resultType)) {
				BinaryOperatorExpr.Operator operator=runtimeOperators.get(resultType);
				if(operator != null) {
					result = new BinaryOperatorExpr(
						operator,
						coerce(expr.getLeft().translate(this), ltype, resultType, false),
						coerce(expr.getRight().translate(this), rtype, resultType, false));
				}
				else
					throw new InternalCompilationStorkException("Implicit binary operator not allowed: "+expr.getOperator());
			}
			else
				throw new NoSuchOperatorException(expr.getOperator().getText(), ltype, rtype);
		}

		return result;
	}
	
	// UNARY OPERATORS ////////////////////////////////////////////////////////
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
	
	public Expr translate(UnaryOperatorExprAST expr) {
		Expr result;
		
		Type type=expr.typeOf(this);
		
		Map<Type,UnaryOperatorExpr.Operator> runtimeOperators=unaryOperators.get(expr.getOperator());
		if(runtimeOperators == null)
			throw new InternalCompilationStorkException("Unrecognized UnaryOperatorExpr.Operator: "+expr.getOperator());
		
		if(runtimeOperators.containsKey(type)) {
			UnaryOperatorExpr.Operator operator=runtimeOperators.get(type);
			if(operator != null) {
				result = new UnaryOperatorExpr(
					operator,
					expr.getChild().translate(this));
			}
			else {
				// If there is an "empty slot" for our runtime operator, that means that
				// this is a valid operation, but has no side-effect. (For example, 
				// unary plus has no effect.) Just return the given expression.
				result = expr.getChild().translate(this);
			}
		}
		else
			throw new NoSuchOperatorException(expr.getOperator().getText(), type);

		return result;
	}
	
	// OTHER //////////////////////////////////////////////////////////////////
	public Expr translate(IntExprAST expr) {
		return new IntExpr(expr.getValue());
	}
	
	public Expr translate(FloatExprAST expr) {
		return new FloatExpr(expr.getValue());
	}
	
	public Expr translate(VarExprAST expr) {
		Expr result;
		
		if(!getGlobe().listSlots().contains(expr.getName()))
			throw new UndefinedVariableException(expr.getName());
		else
		if(!getGlobe().getSlot(expr.getName()).hasFlag(Gamma.Slot.Flag.INITIALIZED))
			throw new UninitializedVariableException(expr.getName());
		else
			result = new VarExpr(expr.getName());
		
		return result;
	}
	
	public Expr translate(CastExprAST expr) {
		Type castType=expr.getType().eval(this);

		Type valueType=expr.getExpr().typeOf(this);
		
		Expr value=expr.getExpr().translate(this);
		
		return coerce(value, valueType, castType, true);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// ASSIGNMENT /////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public Expr assign(BinaryOperatorExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
	
	public Expr assign(FloatExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
	
	public Expr assign(IntExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
	
	public Expr assign(UnaryOperatorExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
	
	public Expr assign(VarExprAST left, ExprAST right) {
		Expr result;
		
		Expr rvalue=right.translate(this);
		
		if(left instanceof VarExprAST) {
			VarExprAST lvalue=(VarExprAST) left;
			if(!getGlobe().listSlots().contains(lvalue.getName()))
				throw new UndefinedVariableException(lvalue.getName());
			else {
				Type ltype=getGlobe().getSlot(lvalue.getName()).getType();
				result = new VarAssignExpr(lvalue.getName(), coerce(rvalue, right.typeOf(this), ltype, false));
				getGlobe().getSlot(lvalue.getName()).setFlag(Gamma.Slot.Flag.INITIALIZED);
			}
		}
		else
			throw new NotAnLValueException(left);
		
		return result;
	}
	
	public Expr assign(CastExprAST left, ExprAST right) {
		throw new NotAnLValueException(left);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// TYPE ///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	public Type typeOf(BinaryOperatorExprAST expr) {
		Type ltype=expr.getLeft().typeOf(this);
		Type rtype=expr.getRight().typeOf(this);
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
	
	public Type typeOf(UnaryOperatorExprAST expr) {
		return expr.getChild().typeOf(this);
	}
	
	public Type typeOf(FloatExprAST expr) {
		return Type.FLOAT;
	}
	
	public Type typeOf(IntExprAST expr) {
		return Type.INT;
	}
	
	public Type typeOf(VarExprAST expr) {
		Type result;
		if(getGlobe().listSlots().contains(expr.getName()))
			result = getGlobe().getSlot(expr.getName()).getType();
		else
			throw new UndefinedVariableException(expr.getName());
		return result;
	}
	
	public Type typeOf(CastExprAST expr) {
		return expr.getType().eval(this);
	}
	
	public Type eval(TypeExpr texpr) {
		if(!getGlobe().listTypes().contains(texpr.getName()))
			throw new UndefinedTypeException(texpr.getName());
		return getGlobe().getType(texpr.getName());
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
}
