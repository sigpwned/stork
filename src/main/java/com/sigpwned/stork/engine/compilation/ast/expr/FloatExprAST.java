	package com.sigpwned.stork.engine.compilation.ast.expr;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.type.numeric.FloatType;
import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.expr.FloatExpr;

public class FloatExprAST extends ExprAST {
	private double value;
	
	public FloatExprAST(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void analyze() {
	}

	public Type getType() {
		return FloatType.FLOAT;
	}

	public Expr compile() {
		return new FloatExpr(getValue());
	}
}
