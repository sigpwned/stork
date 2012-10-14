package com.sigpwned.stork.engine.runtime.stmt;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.Stmt;

public class ReturnStmt extends Stmt {
	private Expr expr;
	
	public ReturnStmt(Expr expr) {
		this.expr = expr;
	}

	public Expr getExpr() {
		return expr;
	}

	public Object exec(Scope scope) {
		Object value=getExpr().eval(scope);
		scope.setReturned(value);
		return null;
	}
}
