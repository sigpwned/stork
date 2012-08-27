package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;

public class IntExpr extends Expr {
	private long value;
	
	public IntExpr(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}

	public Object eval() {
		return Long.valueOf(getValue());
	}
}
