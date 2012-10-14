package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.runtime.Expr;

public class CastExprAST extends ExprAST {
	private TypeExpr type;
	private ExprAST expr;
	
	public CastExprAST(TypeExpr type, ExprAST expr) {
		this.type = type;
		this.expr = expr;
	}
	
	public TypeExpr getType() {
		return type;
	}

	public ExprAST getExpr() {
		return expr;
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
