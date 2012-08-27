package com.sigpwned.stork.ast;

import com.sigpwned.stork.ast.expr.BinaryOperatorExpr;
import com.sigpwned.stork.ast.expr.FloatExpr;
import com.sigpwned.stork.ast.expr.IntExpr;
import com.sigpwned.stork.ast.expr.UnaryOperatorExpr;

public abstract class Expr extends AST {
	public BinaryOperatorExpr asBinop() {
		return (BinaryOperatorExpr) this;
	}

	public UnaryOperatorExpr asUnop() {
		return (UnaryOperatorExpr) this;
	}

	public IntExpr asInt() {
		return (IntExpr) this;
	}

	public FloatExpr asFloat() {
		return (FloatExpr) this;
	}
}
