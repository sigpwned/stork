package com.sigpwned.stork.engine.compilation.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public abstract class AST {
	private List<AST> children;
	
	protected AST() {
	}
	
	public List<AST> getChildren() {
		List<AST> result;
		if(children == null)
			result = Collections.emptyList();
		else
			result = children;
		return result;
	}
	
	protected void addChild(AST child) {
		if(children == null)
			children = new ArrayList<AST>();
		getChildren().add(child);
	}
}
