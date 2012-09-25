package com.sigpwned.stork.engine.compilation;

import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public interface StorkCompiler {
	public Expr compile(BinaryOperatorExprAST expr);

	public Expr compile(UnaryOperatorExprAST expr);

	public Expr compile(IntExprAST expr);

	public Expr compile(FloatExprAST expr);
}