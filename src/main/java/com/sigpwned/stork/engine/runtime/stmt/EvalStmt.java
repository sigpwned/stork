package com.sigpwned.stork.engine.runtime.stmt;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.Stmt;

public class EvalStmt extends Stmt {
	private Expr expr;
	
	public EvalStmt(Expr expr) {
		this.expr = expr;
	}
	
	public Expr getExpr() {
		return expr;
	}

	public Object exec(Scope scope) {
		return getExpr().eval(scope);
	}
}
