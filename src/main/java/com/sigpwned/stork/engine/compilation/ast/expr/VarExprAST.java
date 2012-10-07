package com.sigpwned.stork.engine.compilation.ast.expr;

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
	
	public Expr translate(Translator translate) {
		return translate.translate(this);
	}

	public Expr assign(Translator translate, ExprAST value) {
		return translate.assign(this, value);
	}
	
	public Type typeOf(Translator translate) {
		return translate.typeOf(this);
	}
}
