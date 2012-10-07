package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;

public class FloatToIntExpr extends Expr {
	private Expr inner;
	
	public FloatToIntExpr(Expr inner) {
		this.inner = inner;
	}

	public Expr getInner() {
		return inner;
	}

	public Object eval(Scope scope) {
		Double value=(Double) getInner().eval(scope);
		return Long.valueOf(value.longValue());
	}
}
