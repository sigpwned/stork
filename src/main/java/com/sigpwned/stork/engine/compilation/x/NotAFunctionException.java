package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;

public class NotAFunctionException extends CompilationStorkException {
	private static final long serialVersionUID = 7477553546320355132L;
	public NotAFunctionException(ExprAST expr) {
		super("Not a function: "+expr.getClass().getSimpleName());
	}
}
