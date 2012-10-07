package com.sigpwned.stork.engine.compilation.x;

public class UninitializedVariableException extends CompilationStorkException {
	private static final long serialVersionUID = -1109778091176739404L;

	public UninitializedVariableException(String variableName) {
		super("Variable may not have been initialized: "+variableName);
	}
}
