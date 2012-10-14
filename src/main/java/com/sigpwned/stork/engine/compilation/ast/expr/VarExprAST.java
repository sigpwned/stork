package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class VarExprAST extends ExprAST {
	private String name;
	
	public VarExprAST(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Expr translate(Gamma gamma, Translator translate) {
		return translate.translate(gamma, this);
	}

	public Expr assign(Gamma gamma, Translator translate, ExprAST value) {
		return translate.assign(gamma, this, value);
	}
	
	public Type typeOf(Gamma gamma, Translator translate) {
		return translate.typeOf(gamma, this);
	}
}
