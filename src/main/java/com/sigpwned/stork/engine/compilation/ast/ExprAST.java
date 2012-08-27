package com.sigpwned.stork.engine.compilation.ast;

import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;

public abstract class ExprAST extends AST {
	public BinaryOperatorExprAST asBinaryOperator() {
		return (BinaryOperatorExprAST) this;
	}

	public UnaryOperatorExprAST asUnaryOperator() {
		return (UnaryOperatorExprAST) this;
	}

	public IntExprAST asInt() {
		return (IntExprAST) this;
	}

	public FloatExprAST asFloat() {
		return (FloatExprAST) this;
	}
}
