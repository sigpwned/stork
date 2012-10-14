package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.ast.expr.InvokeExprAST;
import com.sigpwned.stork.engine.compilation.type.FunctionType;

public class ArgumentMismatchException extends CompilationStorkException {
	private static final long serialVersionUID = 6855078120880977498L;
	public ArgumentMismatchException(FunctionType type, InvokeExprAST expr) {
		super("Function expects "+type.numParameterTypes()+" arguments, not "+expr.getArguments().size());
	}
}
