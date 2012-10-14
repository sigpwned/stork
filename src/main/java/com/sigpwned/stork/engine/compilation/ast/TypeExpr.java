package com.sigpwned.stork.engine.compilation.ast;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;


public class TypeExpr {
	private String name;
	
	public TypeExpr(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Type eval(Gamma gamma, Translator translate) {
		return translate.eval(gamma, this);
	}
}
