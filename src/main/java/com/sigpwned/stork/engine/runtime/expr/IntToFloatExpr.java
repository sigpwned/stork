package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;

public class IntToFloatExpr extends Expr {
	private Expr inner;
	
	public IntToFloatExpr(Expr inner) {
		this.inner = inner;
	}
	
	public Expr getInner() {
		return inner;
	}
	
	public Object eval() {
		Long value=(Long) getInner().eval();
		return Double.valueOf(value.doubleValue());
	}
}
