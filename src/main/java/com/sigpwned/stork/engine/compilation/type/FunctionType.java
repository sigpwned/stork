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
	
	public boolean equals(Object other) {
		boolean result;
		
		if(this == other)
			result = true;
		else
		if(other == null)
			result = false;
		else
		if(other instanceof FunctionType) {
			FunctionType otherp=(FunctionType) other;
			if(numParameterTypes() == otherp.numParameterTypes()) {
				result = true;
				for(int i=0;i<numParameterTypes();i++)
					result = result && getParameterType(i).equals(otherp.getParameterType(i));
				result = result && getResultType().equals(otherp.getResultType());
			}
			else
				result = false;
		}
		else
			result = false;
		
		return result;
	}
}
