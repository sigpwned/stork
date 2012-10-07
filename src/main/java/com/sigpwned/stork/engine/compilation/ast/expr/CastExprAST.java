package com.sigpwned.stork.engine.compilation.ast.expr;

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
