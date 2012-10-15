package com.sigpwned.stork.engine.compilation.ast.expr;

import java.util.List;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.ParameterAST;
import com.sigpwned.stork.engine.runtime.Expr;

public class LambdaExprAST extends ExprAST {
	private List<ParameterAST> parameters;
	private ExprAST body;
	
	public LambdaExprAST(List<ParameterAST> parameters, ExprAST body) {
		this.parameters = parameters;
		this.body = body;
	}
	
	protected List<ParameterAST> getParameters() {
		return parameters;
	}
	
	public int numParameters() {
		return getParameters().size();
	}
	
	public ParameterAST getParameter(int index) {
		return getParameters().get(index);
	}

	public ExprAST getBody() {
		return body;
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
