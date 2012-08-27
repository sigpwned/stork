package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;

public class IntExprAST extends ExprAST {
	private long value;
	
	public IntExprAST(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public Type getType() {
		return Type.INT;
	}
}
