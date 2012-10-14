package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.engine.runtime.Value;
import com.sigpwned.stork.engine.runtime.value.Function;
import com.sigpwned.stork.engine.runtime.x.InternalRuntimeStorkException;

public class InvokeExpr extends Expr {
	private Expr function;
	private Expr[] arguments;
	
	public InvokeExpr(Expr function, Expr[] arguments) {
		super();
		this.function = function;
		this.arguments = arguments;
	}

	public Expr getFunction() {
		return function;
	}

	public Expr[] getArguments() {
		return arguments;
	}

	public Object eval(Scope outer) {
		Function function;
		try {
			function = (Function) this.function.eval(outer);
		}
		catch(ClassCastException e) {
			throw new InternalRuntimeStorkException("Expression does not evaluate to a Function: "+this.function);
		}

		if(getArguments().length != function.getParameters().length)
			throw new InternalRuntimeStorkException("Argument/parameter count mismatch; params="+function.getParameters().length+", args="+getArguments().length);
		
		Scope inner=new Scope(function.getOuter());
		for(int i=0;i<getArguments().length;i++)
			inner.defineVar(function.getParameters()[i], getArguments()[i].eval(outer));
		
		function.getBody().exec(inner);
		
		Object result=inner.getReturned();
		if(result == null)
			throw new InternalRuntimeStorkException("Function did not return value: "+function);
		if(result == Value.VOID)
			result = null;
		
		return result;
	}
}
