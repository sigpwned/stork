package com.sigpwned.stork.engine.compilation.ast.stmt;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.runtime.Stmt;

public class ReturnStmtAST extends StmtAST {
	private ExprAST expr;
	
	public ReturnStmtAST(ExprAST expr) {
		this.expr = expr;
	}

	public ExprAST getExpr() {
		return expr;
	}

	public Stmt translate(Gamma gamma, Translator translate) {
		return translate.translate(gamma, this);
	}

	public void defineFunctions(Gamma gamma, Translator translate) {
		translate.defineFunctions(gamma, this);
	}
}
