package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;

public class UnaryOperatorExpr extends Expr {
	public static enum Operator {
		INEG {
			public Object eval(Object value) {
				return Long.valueOf(-((Long)value).longValue());
			}
		},
		FNEG {
			public Object eval(Object value) {
				return Double.valueOf(-((Double)value).doubleValue());
			}
		};
		
		public abstract Object eval(Object value);
	}
	
	private Operator operator;
	private Expr inner;
	
	public UnaryOperatorExpr(Operator operator, Expr inner) {
		this.operator = operator;
		this.inner = inner;
	}
	
	public Operator getOperator() {
		return operator;
	}

	public Expr getInner() {
		return inner;
	}

	public Object eval() {
		return getOperator().eval(getInner().eval());
	}
}