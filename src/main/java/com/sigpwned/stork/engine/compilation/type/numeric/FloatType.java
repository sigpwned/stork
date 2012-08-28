package com.sigpwned.stork.engine.compilation.type.numeric;

import com.sigpwned.stork.engine.compilation.type.NumericType;

public class FloatType extends NumericType {
	public static final FloatType INSTANCE=new FloatType();
	
	protected FloatType() {
	}
	
	public String getText() {
		return "Float";
	}

	public int getPrecision() {
		return 2000;
	}
}
