package com.sigpwned.stork.engine.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sigpwned.stork.engine.runtime.x.InternalRuntimeStorkException;

public class Scope {
	private Map<String,Object> vars;
	
	public Scope() {
		this.vars = new HashMap<String,Object>();
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
		if(!listVars().contains(name))
			throw new InternalRuntimeStorkException("Cannot assign to undefined variable: "+name);
		getVars().put(name, value);
	}
	
	public Object getValue(String name) {
		Object result;
		if(!listVars().contains(name))
			throw new InternalRuntimeStorkException("No variable with name: "+name);
		else {
			result = getVars().get(name);
			if(result == null)
				throw new InternalRuntimeStorkException("Variable has no value: "+name);
		}
		return result;
	}
}
