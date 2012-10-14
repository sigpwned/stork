package com.sigpwned.stork.engine.compilation.x;

public class FunctionMayNotReturnException extends CompilationStorkException {
	private static final long serialVersionUID = 4862934391907707990L;

	public FunctionMayNotReturnException(String functionName) {
		super("Control may reach end of non-Void function: "+functionName);
	}
}
