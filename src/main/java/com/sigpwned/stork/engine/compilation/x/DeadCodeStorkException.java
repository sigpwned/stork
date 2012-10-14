package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.ast.StmtAST;

public class DeadCodeStorkException extends CompilationStorkException {
	private static final long serialVersionUID = 5555724027474625360L;

	public DeadCodeStorkException(StmtAST stmt) {
		super("This code will never be reached: "+stmt.getClass().getSimpleName());
	}
}
