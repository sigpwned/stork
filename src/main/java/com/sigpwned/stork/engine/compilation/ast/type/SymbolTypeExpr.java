package com.sigpwned.stork.engine.compilation.ast.type;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;


public class SymbolTypeExpr extends TypeExpr {
	private String name;
	
	public SymbolTypeExpr(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Type eval(Gamma gamma, Translator translate) {
		return translate.eval(gamma, this);
	}
}
