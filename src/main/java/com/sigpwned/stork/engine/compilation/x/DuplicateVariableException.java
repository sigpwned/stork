package com.sigpwned.stork.engine.compilation.x;

public class DuplicateVariableException extends CompilationStorkException {
	private static final long serialVersionUID = 2759765748394617452L;

	public DuplicateVariableException(String variableName) {
		super("Variable with name already defined: "+variableName);
	}
}
