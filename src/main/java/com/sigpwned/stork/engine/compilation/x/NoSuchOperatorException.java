package com.sigpwned.stork.engine.compilation.x;

import com.sigpwned.stork.engine.compilation.Type;

public class NoSuchOperatorException extends CompilationStorkException {
	private static final long serialVersionUID = 9039173006768833739L;
	
	private String operator;
	private Type[] types;
	
	public NoSuchOperatorException(String operator, Type... types) {
		super("No operator `"+operator+"' that accepts types: "+join(types, ", "));
		this.operator = operator;
		this.types = types;
	}
	
	public String getOperator() {
		return operator;
	}

	public Type[] getTypes() {
		return types;
	}
	
	protected static String join(Object[] objects, String join) {
		StringBuilder result=new StringBuilder();
		if(objects.length != 0) {
			result.append(objects[0].toString());
			for(int i=1;i<objects.length;i++) {
				result.append(join);
				result.append(objects[i].toString());
			}
		}
		return result.toString();
	}
}
