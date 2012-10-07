package com.sigpwned.stork.engine.compilation.ast.stmt;

import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.runtime.Stmt;

public class DeclareStmtAST extends StmtAST {
	private String name;
	private TypeExpr type;
	private ExprAST init;
	
	public DeclareStmtAST(String name, TypeExpr type, ExprAST init) {
		this.name = name;
		this.type = type;
		this.init = init;
	}

	public String getName() {
		return name;
	}

	public TypeExpr getType() {
		return type;
	}

	public ExprAST getInit() {
		return init;
	}

	public Stmt translate(Translator translate) {
		return translate.translate(this);
	}
}
