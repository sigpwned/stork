package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.x.StorkException;

public class BinaryOperatorExprAST extends ExprAST {
	public static enum Operator {
		PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), MOD("%");
		
		private String text;
		
		private Operator(String text) {
			this.text = text;
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
		return (ExprAST) getChildren().get(0);
	}

	public Type getType() {
		return getOperator().getType(getLeft().getType(), getRight().getType());
	}
}
