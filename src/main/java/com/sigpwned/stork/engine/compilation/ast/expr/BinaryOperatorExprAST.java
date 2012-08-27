package com.sigpwned.stork.engine.compilation.ast.expr;

import java.util.HashMap;
import java.util.Map;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.expr.BinaryOperatorExpr;
import com.sigpwned.stork.x.StorkException;

public class BinaryOperatorExprAST extends ExprAST {
	public static enum Operator {
		PLUS("+",   BinaryOperatorExpr.Operator.IADD, BinaryOperatorExpr.Operator.FADD),
		MINUS("-",  BinaryOperatorExpr.Operator.ISUB, BinaryOperatorExpr.Operator.FSUB),
		TIMES("*",  BinaryOperatorExpr.Operator.IMUL, BinaryOperatorExpr.Operator.FMUL),
		DIVIDE("/", BinaryOperatorExpr.Operator.IDIV, BinaryOperatorExpr.Operator.FDIV),
		MOD("%",    BinaryOperatorExpr.Operator.MOD,  null);
		
		private String text;
		private Map<Type,BinaryOperatorExpr.Operator> runtimeOperators;
		
		private Operator(String text, BinaryOperatorExpr.Operator iop, BinaryOperatorExpr.Operator fop) {
			this.text = text;
			this.runtimeOperators = new HashMap<Type,BinaryOperatorExpr.Operator>();
			if(iop != null)
				runtimeOperators.put(Type.INT, iop);
			if(fop != null)
				runtimeOperators.put(Type.FLOAT, fop);
		}
		
		public String getText() {
			return text;
		}
		
		public Type getType(Type left, Type right) {
			if(!left.equals(right))
				throw new NoSuchOperatorException(getText(), left, right);
			
			Type result;
			if(left.equals(Type.INT))
				result = Type.INT;
			else
			if(left.equals(Type.FLOAT))
				result = Type.FLOAT;
			else
				throw new StorkException("Unrecognized type: "+left);
			
			return result;
		}
		
		public BinaryOperatorExpr.Operator getRuntimeOperator(Type left, Type right) {
			Type type=getType(left, right);
			BinaryOperatorExpr.Operator result=runtimeOperators.get(type);
			if(result == null)
				throw new NoSuchOperatorException(getText(), left, right);
			return result;
		}
		
		public Expr compile(ExprAST left, ExprAST right) {
			BinaryOperatorExpr.Operator runtimeOperator=getRuntimeOperator(left.getType(), right.getType());
			return new BinaryOperatorExpr(runtimeOperator, left.compile(), right.compile());
		}
	}
	
	private Operator operator;
	
	public BinaryOperatorExprAST(Operator operator, ExprAST left, ExprAST right) {
		this.operator = operator;
		addChild(left);
		addChild(right);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public ExprAST getLeft() {
		return (ExprAST) getChildren().get(0);
	}

	
	public ExprAST getRight() {
		return (ExprAST) getChildren().get(1);
	}

	public void analyze() {
		getLeft().analyze();
		getRight().analyze();
		getOperator().getRuntimeOperator(getLeft().getType(), getRight().getType());
	}

	public Type getType() {
		return getOperator().getType(getLeft().getType(), getRight().getType());
	}

	public Expr compile() {
		return getOperator().compile(getLeft(), getRight());
	}
}
