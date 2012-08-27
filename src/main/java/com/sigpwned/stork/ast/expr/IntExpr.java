package com.sigpwned.stork.ast.expr;

import com.sigpwned.stork.ast.Expr;

public class IntExpr extends Expr {
	private long value;
	
	public IntExpr(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}
}
