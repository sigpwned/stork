package com.sigpwned.stork.engine.runtime.stmt;

import com.sigpwned.stork.engine.runtime.Block;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.Stmt;
import com.sigpwned.stork.engine.runtime.value.Function;

public class FunctionStmt extends Stmt {
	private String name;
	private String[] params;
	private Block body;
	
	public FunctionStmt(String name, String[] params, Block body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public String[] getParams() {
		return params;
	}

	public Block getBody() {
		return body;
	}

	public Object exec(Scope scope) {
		Function function=new Function(scope, getParams(), getBody());
		scope.defineVar(getName(), function);
		return function;
	}
}