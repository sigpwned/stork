package com.sigpwned.stork.ast.expr;

import com.sigpwned.stork.ast.Expr;

public class BinaryOperatorExpr extends Expr {
	public static enum Operator {
		PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), MOD("%");
		
		private String text;
		
		private Operator(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}
	
	private Operator operator;
	
	public BinaryOperatorExpr(Operator operator, Expr left, Expr right) {
		this.operator = operator;
		addChild(left);
		addChild(right);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Expr getLeft() {
		return (Expr) getChildren().get(0);
	}

	
	public Expr getRight() {
		return (Expr) getChildren().get(0);
	}
}
