package com.sigpwned.stork.engine.compilation.x;

public class UndefinedTypeException extends CompilationStorkException {
	private static final long serialVersionUID = -8412157377621359374L;

	public UndefinedTypeException(String typeName) {
		super("No type with name: "+typeName);
	}
}
