package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;

public class FloatExpr extends Expr {
	private double value;
	
	public FloatExpr(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}

	public Object eval() {
		return Double.valueOf(getValue());
	}
}
