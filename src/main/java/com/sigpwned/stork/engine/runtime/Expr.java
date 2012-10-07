package com.sigpwned.stork.engine.runtime;

public abstract class Expr {
	public abstract Object eval(Scope scope);
}
