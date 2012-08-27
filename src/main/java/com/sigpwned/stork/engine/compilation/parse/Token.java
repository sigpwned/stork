package com.sigpwned.stork.engine.compilation.parse;

public class Token {
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
		
		// Operator Tokens
		PLUS(MetaType.OPERATOR, "+"),
		MINUS(MetaType.OPERATOR, "-"),
		STAR(MetaType.OPERATOR, "*"),
		SLASH(MetaType.OPERATOR, "/"),
		PERCENT(MetaType.OPERATOR, "%"),
		LPAREN(MetaType.OPERATOR, "("),
		RPAREN(MetaType.OPERATOR, ")"),
		
		// Special Tokens
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
	
	public Token(Type type, int offset, String text) {
		this.type = type;
		this.offset = offset;
		this.text = text;
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
}
