package com.sigpwned.stork.ast.expr;

import com.sigpwned.stork.ast.Expr;

public class UnaryOperatorExpr extends Expr {
	public static enum Operator {
		NEGATIVE("-"), POSITIVE("+");
		
		private String text;
		
		private Operator(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}
	
	private Operator operator;
	
	public UnaryOperatorExpr(Operator operator, Expr child) {
		this.operator = operator;
		addChild(child);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Expr getChild() {
		return (Expr) getChildren().get(0);
	}
}
