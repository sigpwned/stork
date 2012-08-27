package com.sigpwned.stork.engine.runtime.expr;

import com.sigpwned.stork.engine.runtime.Expr;
import com.sigpwned.stork.engine.runtime.x.DivideByZeroStorkException;

public class BinaryOperatorExpr extends Expr {
	public static enum Operator {
		IADD {
			public Object eval(Object left, Object right) {
				Long a=(Long) left;
				Long b=(Long) right;
				return Long.valueOf(a.longValue()+b.longValue());
			}
		},
		ISUB {
			public Object eval(Object left, Object right) {
				Long a=(Long) left;
				Long b=(Long) right;
				return Long.valueOf(a.longValue()-b.longValue());
			}
		},
		IMUL {
			public Object eval(Object left, Object right) {
				Long a=(Long) left;
				Long b=(Long) right;
				return Long.valueOf(a.longValue()*b.longValue());
			}
		},
		IDIV {
			public Object eval(Object left, Object right) {
				Long a=(Long) left;
				Long b=(Long) right;
				if(b.longValue() == 0)
					throw new DivideByZeroStorkException();
				return Long.valueOf(a.longValue()/b.longValue());
			}
		},
		MOD {
			public Object eval(Object left, Object right) {
				Long a=(Long) left;
				Long b=(Long) right;
				if(b.longValue() == 0)
					throw new DivideByZeroStorkException();
				return Long.valueOf(a.longValue()%b.longValue());
			}
		},
		FADD {
			public Object eval(Object left, Object right) {
				Double a=(Double) left;
				Double b=(Double) right;
				return Double.valueOf(a.doubleValue()+b.doubleValue());
			}
		},
		FSUB {
			public Object eval(Object left, Object right) {
				Double a=(Double) left;
				Double b=(Double) right;
				return Double.valueOf(a.doubleValue()-b.doubleValue());
			}
		},
		FMUL {
			public Object eval(Object left, Object right) {
				Double a=(Double) left;
				Double b=(Double) right;
				return Double.valueOf(a.doubleValue()*b.doubleValue());
			}
		},
		FDIV {
			public Object eval(Object left, Object right) {
				Double a=(Double) left;
				Double b=(Double) right;
				if(b.doubleValue() == 0.0)
					throw new DivideByZeroStorkException();
				return Double.valueOf(a.doubleValue()/b.doubleValue());
			}
		};
		
		public abstract Object eval(Object left, Object right);
	}
	
	public Object eval() {
		return null;
	}
}
