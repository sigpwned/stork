package com.sigpwned.stork.engine.compilation;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	
	private Map<String,Slot> slots;
	private Map<String,Type> types;
	
	public Gamma() {
		this.slots = new HashMap<String,Slot>();
		this.types = new HashMap<String,Type>();
	}
	
	protected Map<String,Slot> getSlots() {
		return slots;
	}
	
	public Set<String> listSlots() {
		return getSlots().keySet();
	}
	
	public Slot getSlot(String name) {
		Slot result=getSlots().get(name);
		if(result == null)
			throw new InternalCompilationStorkException("No such slot: "+name);
		return result;
	}
	
	public void addSlot(String name, Type type) {
		getSlots().put(name, new Slot(type));
	}
	
	protected Map<String,Type> getTypes() {
		return types;
	}
	
	public Set<String> listTypes() {
		return getTypes().keySet();
	}
	
	public Type getType(String name) {
		Type result=getTypes().get(name);
		if(result == null)
			throw new InternalCompilationStorkException("No such type: "+name);
		return result;
	}
	
	public void addType(String name, Type type) {
		getTypes().put(name, type);
	}
}
