package com.sigpwned.stork.engine.compilation.type;

import com.sigpwned.stork.engine.compilation.Type;

public class FunctionType extends Type {
	private Type resultType;
	private Type[] parameterTypes;
	
	public FunctionType(Type resultType, Type[] parameterTypes) {
		this.resultType = resultType;
		this.parameterTypes = parameterTypes;
	}

	public Type getResultType() {
		return resultType;
	}
	
	public Type[] getParameterTypes() {
		return parameterTypes;
	}
	
	public int numParameterTypes() {
		return getParameterTypes().length;
	}

	public Type getParameterType(int index) {
		return getParameterTypes()[index];
	}

	public String getText() {
		StringBuilder result=new StringBuilder();
		result.append("(");
		if(getParameterTypes().length != 0) {
			result.append(getParameterType(0).toString());
			for(int i=1;i<numParameterTypes();i++) {
				result.append(",");
				result.append(getParameterType(i).toString());
			}
		}
		result.append(")");
		result.append("->");
		result.append(getResultType().toString());
		return result.toString();
	}
}
