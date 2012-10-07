package com.sigpwned.stork.engine.compilation.parse;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sigpwned.stork.io.ParseReader;

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
	private Token next;
	
	public Tokenizer(Reader input) {
		this.input = new ParseReader(input);
		this.next  = null;
	}
	
	protected ParseReader getInput() {
		return input;
	}
	
	public Token peekToken() throws IOException, ParseException {
		if(next == null) {
			int w=w();
			
			int offset=getInput().getOffset();
			
			if(getInput().peek() == -1)
				next = new Token(Token.Type.EOF, offset, Token.Type.EOF.getText());
			else
			if(Character.isLetter(getInput().peek())) {
				StringBuilder buf=new StringBuilder();
				while(Character.isLetter(getInput().peek()))
					buf.append((char) getInput().read());
				
				String text=buf.toString();
				if(keywordTypes.containsKey(text)) {
					Token.Type type=keywordTypes.get(text);
					next = new Token(type, offset, type.getText());
				}
				else
					next = new Token(Token.Type.SYMBOL, offset, text);
			} else
			if(Character.isDigit(getInput().peek())) {
				StringBuilder buf=new StringBuilder();
				while(Character.isDigit(getInput().peek()))
					buf.append((char) getInput().read());
				if(getInput().peek() == '.') {
					buf.append((char) getInput().expect('.'));
					if(Character.isDefined(getInput().peek())) {
						while(Character.isDigit(getInput().peek()))
							buf.append((char) getInput().read());
						next = new Token(Token.Type.FLOAT, offset, buf.toString());
					}
					else
						throw new ParseException("Floating-point numbers must have at least one digit after the decimal point", offset);
				}
				else
					next = new Token(Token.Type.INT, offset, buf.toString());
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
				next = new Token(Token.Type.STRING, offset, buf.toString());
			} else
			if(operatorCharacters.contains(Character.valueOf((char) getInput().peek()))) {
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
				next = new Token(type, offset, type.getText());
			}
			else
				throw new ParseException("Unrecognized character: "+(char) getInput().peek(), getInput().getOffset());
			
			if((w&NEWLINE) != 0)
				next.setFlag(Token.Flag.NEWLINE);
		}
		return next;
	}
	
	public Token.Type peekType() throws IOException, ParseException {
		return peekToken().getType();
	}
	
	public Token nextToken() throws IOException, ParseException {
		Token result=peekToken();
		next = null;
		return result;
	}
	
	public Token consumeType(Token.Type type) throws IOException, ParseException {
		Token result=nextToken();
		if(result.getType() != type)
			throw new ParseException("Expected token `"+type+"', found `"+result.getType()+"'", result.getOffset());
		return result;
	}
	
	private static int SPACE=  1 << 0;
	private static int TAB=    1 << 1;
	private static int NEWLINE=1 << 2;
	protected int w() throws IOException {
		int result=0;
		while(Character.isWhitespace(getInput().peek())) {
			int ch=getInput().read();
			if(ch == ' ')
				result = result | SPACE;
			else
			if(ch == '\t')
				result = result | TAB;
			else
			if(ch=='\n' || ch=='\r')
				result = result | NEWLINE;
		}
		return result;
	}
	
	public void close() throws IOException {
		getInput().close();
	}
}
