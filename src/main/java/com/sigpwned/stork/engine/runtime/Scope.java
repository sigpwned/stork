package com.sigpwned.stork.engine.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sigpwned.stork.engine.runtime.x.InternalRuntimeStorkException;

public class Scope {
	private Scope parent;
	private Map<String,Object> vars;
	private Object returned;
	
	public Scope(Scope parent) {
		this.parent = parent;
		this.vars = new HashMap<String,Object>();
	}

	public Object getReturned() {
		return returned;
	}
	
	public void setReturned(Object returned) {
		this.returned = returned;
	}
	
	public Scope getParent() {
		return parent;
	}
	
	protected Map<String,Object> getVars() {
		return vars;
	}
	
	public Set<String> listVars() {
		return getVars().keySet();
	}
	
	public void defineVar(String name, Object value) {
		if(listVars().contains(name))
			throw new InternalRuntimeStorkException("Cannot re-define variable: "+name);
		getVars().put(name, value);
	}
	
	public void setValue(String name, Object value) {
		if(listVars().contains(name))
			getVars().put(name, value);
		else
		if(getParent() != null)
			getParent().setValue(name, value);
		else
			throw new InternalRuntimeStorkException("Cannot assign to undefined variable: "+name);
	}
	
	public Object getValue(String name) {
		Object result;
		if(listVars().contains(name)) {
			result = getVars().get(name);
			if(result == null)
				throw new InternalRuntimeStorkException("Variable has no value: "+name);
		} else
		if(getParent() != null)
			result = getParent().getValue(name);
		else
			throw new InternalRuntimeStorkException("No variable with name: "+name);
		return result;
	}
}