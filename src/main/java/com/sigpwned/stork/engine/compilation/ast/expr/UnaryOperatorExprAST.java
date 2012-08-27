package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.x.NoSuchOperatorException;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.expr.UnaryOperatorExpr;

public class UnaryOperatorExprAST extends ExprAST {
	public static enum Operator {
		NEGATIVE("-") {
			public Expr compile(ExprAST inner) {
				Type type=getType(inner.getType());
				
				Expr result;
				if(type.equals(Type.FLOAT))
					result = new UnaryOperatorExpr(UnaryOperatorExpr.Operator.FNEG, inner.compile());
				else
				if(type.equals(Type.INT))
					result = new UnaryOperatorExpr(UnaryOperatorExpr.Operator.INEG, inner.compile());
				else
					throw new NoSuchOperatorException(getText(), type);
				
				return result;
			}
		},
		POSITIVE("+") {
			public Expr compile(ExprAST inner) {
				return inner.compile();
			}
		};
		
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
		
		public abstract Expr compile(ExprAST inner);
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

	public void analyze() {
		getChild().analyze();
	}

	public Type getType() {
		return getOperator().getType(getChild().getType());
	}

	public Expr compile() {
		return getOperator().compile(getChild());
	}
}
