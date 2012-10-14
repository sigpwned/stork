package com.sigpwned.stork.engine.runtime.value;

import com.sigpwned.stork.engine.runtime.Block;
import com.sigpwned.stork.engine.runtime.Scope;

public class Function {
	private Scope outer;
	private String[] parameters;
	private Block body;
	
	public Function(Scope outer, String[] parameters, Block body) {
		this.outer = outer;
		this.parameters = parameters;
		this.body = body;
	}
	
	public Scope getOuter() {
		return outer;
	}

	public String[] getParameters() {
		return parameters;
	}

	public Block getBody() {
		return body;
	}
	
	public String toString() {
		return "Function";
	}
}
