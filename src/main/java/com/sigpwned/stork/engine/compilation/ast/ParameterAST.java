package com.sigpwned.stork.engine.compilation.ast;


public class ParameterAST extends AST {
	private TypeExpr type;
	private String name;

	public ParameterAST(TypeExpr type, String name) {
		this.type = type;
		this.name = name;
	}

	public TypeExpr getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}