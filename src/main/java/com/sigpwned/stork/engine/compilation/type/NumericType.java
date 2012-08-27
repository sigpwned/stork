package com.sigpwned.stork.engine.compilation.type;

import com.sigpwned.stork.engine.compilation.Type;
import com.sigpwned.stork.engine.compilation.type.numeric.FloatType;
import com.sigpwned.stork.engine.compilation.type.numeric.IntType;

public abstract class NumericType extends Type {
	public static final FloatType FLOAT=FloatType.INSTANCE;
	public static final IntType INT=IntType.INSTANCE;
}
