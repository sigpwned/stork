package com.sigpwned.stork.engine.compilation.ast;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

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
	
	public abstract Expr translate(Gamma gamma, Translator translate);
	
	public abstract Expr assign(Gamma gamma, Translator translate, ExprAST value);
	
	public abstract Type typeOf(Gamma gamma, Translator translate);	
}
