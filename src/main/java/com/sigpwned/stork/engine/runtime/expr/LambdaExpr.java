package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Block;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.value.Function;

public class LambdaExpr extends Expr {
	private String[] parameterNames;
	private Block body;
	
	public LambdaExpr(String[] parameterNames, Block body) {
		this.parameterNames = parameterNames;
		this.body = body;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public Block getBody() {
		return body;
	}
	
	public Object eval(Scope scope) {
		return new Function(scope, getParameterNames(), getBody());
	}
}
