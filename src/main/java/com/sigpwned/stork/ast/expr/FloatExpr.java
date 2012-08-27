	package com.sigpwned.stork.ast.expr;

import com.sigpwned.stork.ast.Expr;

public class FloatExpr extends Expr {
	private double value;
	
	public FloatExpr(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}
}
