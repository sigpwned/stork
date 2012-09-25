package com.sigpwned.stork.engine.compilation.compiler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import com.sigpwned.stork.engine.compilation.StorkCompiler;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.type.NumericType;
import com.sigpwned.stork.engine.compilation.x.InternalCompilationStorkException;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.expr.BinaryOperatorExpr;
import com.sigpwned.stork.engine.runtime.expr.FloatExpr;
import com.sigpwned.stork.engine.runtime.expr.IntExpr;
import com.sigpwned.stork.engine.runtime.expr.IntToFloatExpr;
import com.sigpwned.stork.engine.runtime.expr.UnaryOperatorExpr;
import com.sigpwned.stork.x.StorkException;

public class DefaultStorkCompiler implements StorkCompiler {
	public DefaultStorkCompiler() {
	}
	
	///////////////////////////////////////////////////////////////////////////
	// BINARY OPERATORS ///////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
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
	
	/* (non-Javadoc)
	 * @see com.sigpwned.stork.engine.compilation.StorkCompiler#compile(com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST)
	 */
	public Expr compile(BinaryOperatorExprAST expr) {
		Expr result;
		
		Type ltype=computeType(expr.getLeft());
		Type rtype=computeType(expr.getRight());
		Type resultType=computeBinaryOperatorResultType(ltype, rtype);
		
		Map<Type,BinaryOperatorExpr.Operator> runtimeOperators=binaryOperators.get(expr.getOperator());
		if(runtimeOperators == null)
			throw new InternalCompilationStorkException("Unrecognized BinaryOperatorExpr.Operator: "+expr.getOperator());
		
		if(runtimeOperators.containsKey(resultType)) {
			BinaryOperatorExpr.Operator operator=runtimeOperators.get(resultType);
			if(operator != null) {
				result = new BinaryOperatorExpr(
					operator,
					coerce(expr.getLeft().compile(this), ltype, resultType),
					coerce(expr.getRight().compile(this), rtype, resultType));
			}
			else
				throw new InternalCompilationStorkException("Implicit binary operator not allowed: "+expr.getOperator());
		}
		else
			throw new NoSuchOperatorException(expr.getOperator().getText(), ltype, rtype);

		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// UNARY OPERATORS ////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
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
	
	/* (non-Javadoc)
	 * @see com.sigpwned.stork.engine.compilation.StorkCompiler#compile(com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST)
	 */
	public Expr compile(UnaryOperatorExprAST expr) {
		Expr result;
		
		Type type=computeType(expr.getChild());
		
		Map<Type,UnaryOperatorExpr.Operator> runtimeOperators=unaryOperators.get(expr.getOperator());
		if(runtimeOperators == null)
			throw new InternalCompilationStorkException("Unrecognized UnaryOperatorExpr.Operator: "+expr.getOperator());
		
		if(runtimeOperators.containsKey(type)) {
			UnaryOperatorExpr.Operator operator=runtimeOperators.get(type);
			if(operator != null) {
				result = new UnaryOperatorExpr(
					operator,
					expr.getChild().compile(this));
			}
			else {
				// If there is an "empty slot" for our runtime operator, that means that
				// this is a valid operation, but has no side-effect. (For example, 
				// unary plus has no effect.) Just return the given expression.
				result = expr.getChild().compile(this);
			}
		}
		else
			throw new NoSuchOperatorException(expr.getOperator().getText(), type);

		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// OTHER //////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	/* (non-Javadoc)
	 * @see com.sigpwned.stork.engine.compilation.StorkCompiler#compile(com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST)
	 */
	public Expr compile(IntExprAST expr) {
		return new IntExpr(expr.getValue());
	}
	
	/* (non-Javadoc)
	 * @see com.sigpwned.stork.engine.compilation.StorkCompiler#compile(com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST)
	 */
	public Expr compile(FloatExprAST expr) {
		return new FloatExpr(expr.getValue());
	}
	
	///////////////////////////////////////////////////////////////////////////
	// TYPE ///////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	protected Type computeType(ExprAST expr) {
		Type result;
		
		if(expr instanceof BinaryOperatorExprAST) {
			Type ltype=computeType(expr.asBinaryOperator().getLeft());
			Type rtype=computeType(expr.asBinaryOperator().getRight());
			result = computeBinaryOperatorResultType(ltype, rtype);
		} else
		if(expr instanceof UnaryOperatorExprAST)
			result = computeType(expr.asUnaryOperator().getChild());
		else
		if(expr instanceof FloatExprAST)
			result = Type.FLOAT;
		else
		if(expr instanceof IntExprAST)
			result = Type.INT;
		else
			throw new InternalCompilationStorkException("Unrecognized ExprAST: "+expr);
		
		return result;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// UTILITY ////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	protected Type computeBinaryOperatorResultType(Type left, Type right) {
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
	
	protected static Expr coerce(Expr expr, Type type, Type target) {
		Expr result;

		if(type.equals(target))
			result = expr;
		else
		if(type instanceof NumericType && target instanceof NumericType) {
			NumericType ntype=(NumericType) type;
			NumericType ntarget=(NumericType) target;
			if(ntype.getPrecision() < ntarget.getPrecision()) {
				if(ntype.equals(Type.INT) && ntarget.equals(Type.FLOAT)) {
					result = new IntToFloatExpr(expr);
				}
				else
					throw new InternalCompilationStorkException("Asked to coerce unrecognized numeric type(s) `"+type.getText()+"' to `"+target.getText()+"'"); 
			}
			else
				throw new InternalCompilationStorkException("Asked to coerce type `"+type.getText()+"' to less precise type `"+target.getText()+"'");
		}
		else
			throw new InternalCompilationStorkException("Asked to perform impossible coercion of type `"+type.getText()+"' to type `"+target.getText()+"'");
		
		return result;
	}
}
