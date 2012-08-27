package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.expr.IntExpr;

public class IntExprAST extends ExprAST {
	private long value;
	
	public IntExprAST(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void analyze() {
	}

	public Type getType() {
		return Type.INT;
	}

	public Expr compile() {
		return new IntExpr(getValue());
	}
}
