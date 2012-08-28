package com.sigpwned.stork.engine.compilation.type.numeric;

import com.sigpwned.stork.engine.compilation.type.NumericType;

public class IntType extends NumericType {
	public static final IntType INSTANCE=new IntType();
	
	public String getText() {
		return "Int";
	}

	public int getPrecision() {
		return 1000;
	}
}
