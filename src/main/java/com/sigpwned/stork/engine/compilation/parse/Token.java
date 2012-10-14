package com.sigpwned.stork.engine.compilation.parse;

import java.util.EnumSet;
import java.util.Set;

public class Token {
	public static enum Flag {
		PRENEWLINE, POSTNEWLINE;
	}
	
	public static enum MetaType {
		VALUE, KEYWORD, OPERATOR, SPECIAL;
	}
	
	public static enum Type {
		// Value Tokens
		INT(MetaType.VALUE, null),
		FLOAT(MetaType.VALUE, null),
		STRING(MetaType.VALUE, null),
		SYMBOL(MetaType.VALUE, null),
		
		// Keyword Tokens
		IF(MetaType.KEYWORD, "if"),
		WHILE(MetaType.KEYWORD, "while"),
		TRUE(MetaType.KEYWORD, "true"),
		FALSE(MetaType.KEYWORD, "false"),
		NULL(MetaType.KEYWORD, "null"),
		VAR(MetaType.KEYWORD, "var"),
		CAST(MetaType.KEYWORD, "cast"),
		FUN(MetaType.KEYWORD, "fun"),
		END(MetaType.KEYWORD, "end"),
		RETURN(MetaType.KEYWORD, "return"),
		LAMBDA(MetaType.KEYWORD, "lambda"),
		VOID(MetaType.KEYWORD, "Void"),
		
		// Operator Tokens
		PLUS(MetaType.OPERATOR, "+"),
		MINUS(MetaType.OPERATOR, "-"),
		STAR(MetaType.OPERATOR, "*"),
		SLASH(MetaType.OPERATOR, "/"),
		PERCENT(MetaType.OPERATOR, "%"),
		LPAREN(MetaType.OPERATOR, "("),
		RPAREN(MetaType.OPERATOR, ")"),
		EQ(MetaType.OPERATOR, "="),
		COMMA(MetaType.OPERATOR, ","),
		ARROW(MetaType.OPERATOR, "->"),
		
		// Separator Tokens
		COLON(MetaType.OPERATOR, ":"),
		SEMICOLON(MetaType.OPERATOR, ";"),
		
		// Special Tokens
		BOF(MetaType.OPERATOR, "^"),
		EOF(MetaType.OPERATOR, "$");
		
		private MetaType type;
		private String text;
		
		private Type(MetaType type, String text) {
			this.type = type;
			this.text = text;
		}

		public MetaType getType() {
			return type;
		}

		public String getText() {
			return text;
		}
	}
	
	private Type type;
	private int offset;
	private String text;
	private Set<Flag> flags;
	
	public Token(Type type, int offset, String text) {
		this.type = type;
		this.offset = offset;
		this.text = text;
		this.flags = EnumSet.noneOf(Flag.class);
	}

	public Type getType() {
		return type;
	}
	
	public int getOffset() {
		return offset;
	}

	public String getText() {
		return text;
	}
	
	protected Set<Flag> getFlags() {
		return flags;
	}
	
	public void setFlag(Flag flag) {
		getFlags().add(flag);
	}
	
	public boolean hasFlag(Flag flag) {
		return getFlags().contains(flag);
	}
}
