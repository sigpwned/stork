package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;

public class UnaryOperatorExprAST extends ExprAST {
	public static enum Operator {
		NEGATIVE("-"), POSITIVE("+");
		
		private String text;
		
		private Operator(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
		
		public Type getType(Type inner) {
			return inner;
		}
	}
	
	private Operator operator;
	
	public UnaryOperatorExprAST(Operator operator, ExprAST child) {
		this.operator = operator;
		addChild(child);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public ExprAST getChild() {
		return (ExprAST) getChildren().get(0);
	}

	public Type getType() {
		return getOperator().getType(getChild().getType());
	}
}
