package com.sigpwned.stork.engine.runtime;

public class Block {
	private Stmt[] body;
	
	public Block(Stmt[] body) {
		this.body = body;
	}

	public Stmt[] getBody() {
		return body;
	}
	
	public void exec(Scope scope) {
		for(int i=0;i<getBody().length;i++) {
			getBody()[i].exec(scope);
			if(scope.getReturned() != null)
				break;
		}
	}
}
