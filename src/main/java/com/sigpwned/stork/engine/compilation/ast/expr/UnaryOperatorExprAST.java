package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.ASTCompiler;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

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

	public Expr compile(ASTCompiler compiler) {
		return compiler.compile(this);
	}
}
