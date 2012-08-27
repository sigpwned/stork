package com.sigpwned.stork.ast;

import java.util.ArrayList;
import java.util.List;



public abstract class AST {
	private List<AST> children;
	
	protected AST() {
		this.children = new ArrayList<AST>();
	}
	
	public List<AST> getChildren() {
		return children;
	}
	
	protected void addChild(AST child) {
		getChildren().add(child);
	}
}
