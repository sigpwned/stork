package com.sigpwned.stork.engine.compilation.ast;

import java.util.List;

public class BlockAST extends AST {
	public BlockAST() {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<StmtAST> getBody() {
		return (List) getChildren();
	}
	
	public void addBody(StmtAST stmt) {
		addChild(stmt);
	}
}
