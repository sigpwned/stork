package com.sigpwned.stork.engine.runtime.x;

public class DivideByZeroStorkException extends RuntimeStorkException {
	private static final long serialVersionUID = -6231418064851078181L;

	public DivideByZeroStorkException() {
		super("Division or modulus by zero");
	}
}
