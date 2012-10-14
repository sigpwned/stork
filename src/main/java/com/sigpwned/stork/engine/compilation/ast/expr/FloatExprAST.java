	package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class FloatExprAST extends ExprAST {
	private double value;
	
	public FloatExprAST(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public Expr translate(Gamma gamma, Translator translator) {
		return translator.translate(gamma, this);
	}

	public Expr assign(Gamma gamma, Translator translate, ExprAST value) {
		return translate.assign(gamma, this, value);
	}
	
	public Type typeOf(Gamma gamma, Translator translate) {
		return translate.typeOf(gamma, this);
	}
}
