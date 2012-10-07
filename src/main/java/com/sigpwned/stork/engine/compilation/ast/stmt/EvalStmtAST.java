package com.sigpwned.stork.engine.compilation.ast.stmt;

import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.runtime.Stmt;

public class EvalStmtAST extends StmtAST {
	private ExprAST expr;
	
	public EvalStmtAST(ExprAST expr) {
		this.expr = expr;
	}
	
	public ExprAST getExpr() {
		return expr;
	}

	public Stmt translate(Translator translate) {
		return translate.translate(this);
	}
}
