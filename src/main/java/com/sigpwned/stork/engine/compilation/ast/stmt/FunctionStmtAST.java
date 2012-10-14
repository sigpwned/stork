package com.sigpwned.stork.engine.compilation.ast.stmt;

import java.util.List;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.BlockAST;
import com.sigpwned.stork.engine.compilation.ast.ParameterAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.runtime.Stmt;

public class FunctionStmtAST extends StmtAST {
	private TypeExpr resultType;
	private String name;
	private List<ParameterAST> parameters;
	private BlockAST body;
	
	public FunctionStmtAST(TypeExpr resultType, String name, List<ParameterAST> parameters, BlockAST body) {
		this.resultType = resultType;
		this.name = name;
		this.parameters = parameters;
		this.body = body;
	}

	public TypeExpr getResultType() {
		return resultType;
	}
	
	public String getName() {
		return name;
	}

	public List<ParameterAST> getParameters() {
		return parameters;
	}

	public BlockAST getBody() {
		return body;
	}

	public Stmt translate(Gamma gamma, Translator translate) {
		return translate.translate(gamma, this);
	}

	public void defineFunctions(Gamma gamma, Translator translate) {
		translate.defineFunctions(gamma, this);
	}
}
