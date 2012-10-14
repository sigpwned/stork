package com.sigpwned.stork.engine.compilation.ast.type;

import java.util.List;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;

public class FunctionTypeExpr extends TypeExpr {
	private List<TypeExpr> parameterTypes;
	private TypeExpr resultType;
	
	public FunctionTypeExpr(TypeExpr resultType, List<TypeExpr> parameterTypes) {
		this.parameterTypes = parameterTypes;
		this.resultType = resultType;
	}

	public List<TypeExpr> getParameterTypes() {
		return parameterTypes;
	}

	public TypeExpr getResultType() {
		return resultType;
	}

	public Type eval(Gamma gamma, Translator translate) {
		return translate.eval(gamma, this);
	}
}
