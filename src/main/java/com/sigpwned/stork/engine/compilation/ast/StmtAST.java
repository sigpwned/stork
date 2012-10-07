package com.sigpwned.stork.engine.compilation.ast;

import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.stmt.DeclareStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.EvalStmtAST;
import com.sigpwned.stork.engine.runtime.Stmt;

public abstract class StmtAST extends AST {
	public EvalStmtAST asEval() {
		return (EvalStmtAST) this;
	}
	
	public DeclareStmtAST asDeclare() {
		return (DeclareStmtAST) this;
	}
	
	public abstract Stmt translate(Translator translate);
}
