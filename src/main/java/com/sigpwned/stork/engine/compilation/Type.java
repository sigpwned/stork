package com.sigpwned.stork.engine.compilation;

import com.sigpwned.stork.engine.compilation.type.NumericType;
import com.sigpwned.stork.engine.compilation.type.numeric.FloatType;
import com.sigpwned.stork.engine.compilation.type.numeric.IntType;

public abstract class Type {
	public static final FloatType FLOAT=NumericType.FLOAT;
	public static final IntType INT=NumericType.INT;
	public static final Type VOID=new Type() {
		public String getText() {
			return "void";
		}
	};
	
	public abstract String getText();
	
	public String toString() {
		return getText();
	}
}
