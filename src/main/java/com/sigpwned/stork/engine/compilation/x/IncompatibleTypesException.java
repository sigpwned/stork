package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.Type;

public class IncompatibleTypesException extends CompilationStorkException {
	private static final long serialVersionUID = -3557729349571259601L;

	public IncompatibleTypesException(Type from, Type to) {
		super("Cannot coerce to incompatible type: "+from+" -> "+to);
	}
}
