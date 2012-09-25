	package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.StorkCompiler;
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

	public Expr compile(StorkCompiler compiler) {
		return compiler.compile(this);
	}
}
