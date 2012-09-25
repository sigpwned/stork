package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.StorkCompiler;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class IntExprAST extends ExprAST {
	private long value;
	
	public IntExprAST(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public Expr compile(StorkCompiler compiler) {
		return compiler.compile(this);
	}
}
