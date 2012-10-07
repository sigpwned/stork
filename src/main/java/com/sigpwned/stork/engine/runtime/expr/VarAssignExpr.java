package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;

public class VarAssignExpr extends Expr {
	private String name;
	private Expr value;
	
	public VarAssignExpr(String name, Expr value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public Expr getValue() {
		return value;
	}

	public Object eval(Scope scope) {
		Object result=getValue().eval(scope);
		scope.setValue(getName(), result);
		return result;
	}
}
