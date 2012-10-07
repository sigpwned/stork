package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;

public class VarExpr extends Expr {
	private String name;
	
	public VarExpr(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public Object eval(Scope scope) {
		return scope.getValue(name);
	}
}
