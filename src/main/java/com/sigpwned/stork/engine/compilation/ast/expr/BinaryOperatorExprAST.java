package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class BinaryOperatorExprAST extends ExprAST {
	public static enum Operator {
		PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/"), MOD("%"), EQ("=");
		
		private String text;
		
		private Operator(String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
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

	public Expr translate(Translator translator) {
		return translator.translate(this);
	}

	public Expr assign(Translator translate, ExprAST value) {
		return translate.assign(this, value);
	}
	
	public Type typeOf(Translator translate) {
		return translate.typeOf(this);
	}

}
