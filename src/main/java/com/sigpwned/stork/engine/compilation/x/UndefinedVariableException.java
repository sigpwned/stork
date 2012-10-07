package com.sigpwned.stork.engine.compilation.x;

public class UndefinedVariableException extends CompilationStorkException {
	private static final long serialVersionUID = 3315786041168194441L;

	public UndefinedVariableException(String variableName) {
		super("No variable with name: "+variableName);
	}
}
