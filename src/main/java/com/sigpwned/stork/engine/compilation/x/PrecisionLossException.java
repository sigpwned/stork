package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.Type;

public class PrecisionLossException extends CompilationStorkException {
	private static final long serialVersionUID = 5392552983163684018L;

	public PrecisionLossException(Type from, Type to) {
		super("Will not coerce to less precise type: "+from+" -> "+to);
	}
}
