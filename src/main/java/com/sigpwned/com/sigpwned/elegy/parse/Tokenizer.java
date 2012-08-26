package com.sigpwned.com.sigpwned.elegy.parse;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sigpwned.elegy.io.ParseReader;

public class Tokenizer {
	private static final Map<String,Token.Type> keywordTypes;
	static {
		keywordTypes = new HashMap<String,Token.Type>();
		for(Token.Type type : Token.Type.values())
			if(type.getType() == Token.MetaType.KEYWORD)
				keywordTypes.put(type.getText(), type);
	}
	
	private static final Set<Character> operatorCharacters;
	private static final Map<String,Token.Type> operatorTypes;
	static {
		operatorCharacters = new HashSet<Character>();
		operatorTypes = new HashMap<String,Token.Type>();
		for(Token.Type type : Token.Type.values())
			if(type.getType() == Token.MetaType.OPERATOR) {
				operatorTypes.put(type.getText(), type);
				for(int i=0;i<type.getText().length();i++)
					operatorCharacters.add(Character.valueOf(type.getText().charAt(i)));
			}
	}
	
	private static final Map<Character,Character> stringEscapes;
	static {
		stringEscapes = new HashMap<Character,Character>();
		stringEscapes.put('n',  '\n');
		stringEscapes.put('r',  '\r');
		stringEscapes.put('f',  '\f');
		stringEscapes.put('t',  '\t');
		stringEscapes.put('b',  '\b');
		stringEscapes.put('\\', '\\');
	}
	
	private ParseReader input;
	
	public Tokenizer(Reader input) {
		this.input = new ParseReader(input);
	}
	
	protected ParseReader getInput() {
		return input;
	}
	
	public Token nextToken() throws IOException, ParseException {
		Token result;
		
		w();
		
		if(getInput().peek() == -1)
			result = new Token(Token.Type.EOF, Token.Type.EOF.getText());
		else
		if(Character.isLetter(getInput().peek())) {
			StringBuilder buf=new StringBuilder();
			while(Character.isLetter(getInput().peek()))
				buf.append((char) getInput().read());
			
			String text=buf.toString();
			if(keywordTypes.containsKey(text)) {
				Token.Type type=keywordTypes.get(text);
				result = new Token(type, type.getText());
			}
			else
				result = new Token(Token.Type.SYMBOL, text);
		} else
		if(Character.isDigit(getInput().peek())) {
			int offset=getInput().getOffset();
			StringBuilder buf=new StringBuilder();
			while(Character.isDigit(getInput().peek()))
				buf.append((char) getInput().read());
			if(getInput().peek() == '.') {
				buf.append((char) getInput().expect('.'));
				if(Character.isDefined(getInput().peek())) {
					while(Character.isDigit(getInput().peek()))
						buf.append((char) getInput().read());
					result = new Token(Token.Type.FLOAT, buf.toString());
				}
				else
					throw new ParseException("Floating-point numbers must have at least one digit after the decimal point", offset);
			}
			else
				result = new Token(Token.Type.INT, buf.toString());
		} else
		if(getInput().peek() == '\"') {
			StringBuilder buf=new StringBuilder();
			getInput().expect('\"');
			while(getInput().peek()!=-1 && getInput().peek()!='\n' && getInput().peek()!='\"') {
				char ch=(char) getInput().read();
				if(ch == '\\') {
					if(getInput().peek() != -1) {
						if(stringEscapes.containsKey((char) getInput().peek()))
							buf.append(stringEscapes.get((char) getInput().read()));
						else
							throw new ParseException("Unrecognized escape sequence: "+(char) getInput().peek(), getInput().getOffset());
					}
					else
						throw new ParseException("Unexpected EOF", getInput().getOffset());
				}
				else
					buf.append(ch);
			}
			getInput().expect('\"');
			result = new Token(Token.Type.STRING, buf.toString());
		} else
		if(operatorCharacters.contains(Character.valueOf((char) getInput().peek()))) {
			int offset=getInput().getOffset();
			StringBuilder buf=new StringBuilder();
			buf.append((char) getInput().peek());
			while(operatorTypes.containsKey(buf.toString())) {
				getInput().read();
				if(getInput().peek() != -1)
					buf.append((char) getInput().peek());
				else {
					buf.append(' ');
					break;
				}
			}
			buf.setLength(buf.length()-1);
			if(!operatorTypes.containsKey(buf.toString()))
				throw new ParseException("Unrecognized operator: "+buf.toString(), offset);
			Token.Type type=operatorTypes.get(buf.toString());
			result = new Token(type, type.getText());
		}
		else
			throw new ParseException("Unrecognized character: "+(char) getInput().peek(), getInput().getOffset());
		
		return result;
	}
	
	protected void w() throws IOException {
		while(Character.isWhitespace(getInput().peek()))
			getInput().read();
	}
	
	public void close() throws IOException {
		getInput().close();
	}
}
