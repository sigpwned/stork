package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.ast.stmt.ReturnStmtAST;

public class ReturnNotInFunctionException extends CompilationStorkException {
	private static final long serialVersionUID = -3140044713762463651L;
	
	public ReturnNotInFunctionException(ReturnStmtAST stmt) {
		super("Return statement is only valid within a function body");
	}
}
