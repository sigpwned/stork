package com.sigpwned.stork.engine.compilation.ast.expr;

import java.util.ArrayList;
import java.util.List;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class InvokeExprAST extends ExprAST {
	private ExprAST function;
	private List<ExprAST> arguments;
	
	public InvokeExprAST(ExprAST function) {
		this.function = function;
		this.arguments = new ArrayList<ExprAST>();
	}
	
	public ExprAST getFunction() {
		return function;
	}
	
	public List<ExprAST> getArguments() {
		return arguments;
	}

	public void addArgument(ExprAST argument) {
		getArguments().add(argument);
	}

	public Expr translate(Gamma gamma, Translator translate) {
		return translate.translate(gamma, this);
	}

	public Expr assign(Gamma gamma, Translator translate, ExprAST value) {
		return translate.assign(gamma, this, value);
	}

	public Type typeOf(Gamma gamma, Translator translate) {
		return translate.typeOf(gamma, this);
	}
}
