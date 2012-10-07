package com.sigpwned.stork.engine.runtime.stmt;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.Stmt;

public class DeclareStmt extends Stmt {
	private String name;
	private Expr init;
	
	public DeclareStmt(String name, Expr init) {
		this.name = name;
		this.init = init;
	}
	
	public String getName() {
		return name;
	}
	
	public Expr getInit() {
		return init;
	}
	
	public Object exec(Scope scope) {
		Object value;
		if(getInit() != null)
			value = getInit().eval(scope);
		else
			value = null;
		scope.defineVar(getName(), value);
		return value;
	}
}
