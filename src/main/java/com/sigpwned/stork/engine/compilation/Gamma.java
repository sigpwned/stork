package com.sigpwned.stork.engine.compilation;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sigpwned.stork.engine.compilation.type.FunctionType;
import com.sigpwned.stork.engine.compilation.x.InternalCompilationStorkException;

public class Gamma {
	public static class Slot {
		public static enum Flag {
			INITIALIZED;
		}
		
		private Type type;
		private Set<Flag> flags;
		
		protected Slot(Type type) {
			this.type = type;
			this.flags = EnumSet.noneOf(Flag.class);
		}

		public Type getType() {
			return type;
		}
		
		protected Set<Flag> getFlags() {
			return flags;
		}
		
		public boolean hasFlag(Flag flag) {
			return getFlags().contains(flag);
		}
		
		public void setFlag(Flag flag) {
			getFlags().add(flag);
		}
	}
	
	public static enum Flag {
		RETURNED;
	}
	
	private Gamma parent;
	private FunctionType functionType;
	private Set<Flag> flags;
	private Map<String,Slot> slots;
	private Map<String,Type> types;
	
	public Gamma(Gamma parent, FunctionType functionType) {
		this.parent = parent;
		this.functionType = functionType;
		this.flags = EnumSet.noneOf(Flag.class);
		this.slots = new HashMap<String,Slot>();
		this.types = new HashMap<String,Type>();
	}
	
	public Gamma getParent() {
		return parent;
	}
	
	public FunctionType getFunctionType() {
		return functionType;
	}
	
	protected Set<Flag> getFlags() {
		return flags;
	}
	
	public void addFlag(Flag flag) {
		getFlags().add(flag);
	}
	
	public boolean hasFlag(Flag flag) {
		return getFlags().contains(flag);
	}
	
	protected Map<String,Slot> getSlots() {
		return slots;
	}
	
	public Set<String> listSlots() {
		return getSlots().keySet();
	}
	
	public Slot getSlot(String name) {
		Slot result;
		if(listSlots().contains(name))
			result = getSlots().get(name);
		else
		if(getParent() != null)
			result = getParent().getSlot(name);
		else
			throw new InternalCompilationStorkException("No such slot: "+name);
		return result;
	}
	
	public Slot addSlot(String name, Type type) {
		Slot result=new Slot(type);
		getSlots().put(name, result);
		return result;
	}
	
	protected Map<String,Type> getTypes() {
		return types;
	}
	
	public Set<String> listTypes() {
		return getTypes().keySet();
	}
	
	public boolean hasType(String name) {
		boolean result;
		try {
			getType(name);
			result = true;
		}
		catch(InternalCompilationStorkException e) {
			result = false;
		}
		return result;
	}
	
	public Type getType(String name) {
		Type result;
		if(listTypes().contains(name))
			result = getTypes().get(name);
		else
		if(getParent() != null)
			result = getParent().getType(name);
		else
			throw new InternalCompilationStorkException("No such type: "+name);
		return result;
	}
	
	public void addType(String name, Type type) {
		getTypes().put(name, type);
	}
}
