package com.sigpwned.stork.engine.compilation.ast;

import com.sigpwned.stork.engine.compilation.Gamma;
import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.Type;


public abstract class TypeExpr {
	public abstract Type eval(Gamma gamma, Translator translate);
}
