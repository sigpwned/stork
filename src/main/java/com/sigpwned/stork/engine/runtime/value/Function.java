package com.sigpwned.stork.engine.runtime.value;

import com.sigpwned.stork.engine.runtime.Block;
import com.sigpwned.stork.engine.runtime.Scope;

public class Function {
	private Scope outer;
	private String[] parameterNames;
	private Block body;
	
	public Function(Scope outer, String[] parameterNames, Block body) {
		this.outer = outer;
		this.parameterNames = parameterNames;
		this.body = body;
	}
	
	public Scope getOuter() {
		return outer;
	}

	public String[] getParameterNames() {
		return parameterNames;
	}

	public Block getBody() {
		return body;
	}
	
	public String toString() {
		return "Function";
	}
}
