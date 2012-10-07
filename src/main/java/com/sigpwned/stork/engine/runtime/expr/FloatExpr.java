package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;

public class FloatExpr extends Expr {
	private double value;
	
	public FloatExpr(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}

	public Object eval(Scope scope) {
		return Double.valueOf(getValue());
	}
}
