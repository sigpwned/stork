package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;

public class NotAnLValueException extends CompilationStorkException {
	private static final long serialVersionUID = -2267483018241421701L;

	public NotAnLValueException(ExprAST lvalue) {
		super("Expression is not a valid lvalue: "+lvalue.getClass().getSimpleName());
	}
}
