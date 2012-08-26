package com.sigpwned.elegy.io;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

public class ParseReader extends Reader {
	private static final int NONE=-2;
	
	private Reader inner;
	private int next;
	private int offset;
	
	public ParseReader(Reader inner) {
		this.inner = inner;
		this.next  = NONE;
	}
	
	public int expect(char want) throws IOException, ParseException {
		int result=read();
		if(result != want)
			throw new ParseException("Expected character `"+want+"', got `"+(char) result+"'", getOffset());
		return result;
	}
	
	public int peek() throws IOException {
		if(next == NONE) {
			next   = getInner().read();
			offset = offset+1;
		}
		return next;
	}
	
	public int read() throws IOException {
		int result=peek();
		next = NONE;
		return result;
	}
	
	public int read(char[] cbuf, int off, int len) throws IOException {
		int result=0;
		for(int i=0;i<len;i++) {
			int ch=read();
			if(ch != -1) {
				cbuf[off+i] = (char) ch;
				result      = result+1;
			}
			else
				break;
		}
		return result;
	}
	
	public int getOffset() {
		return offset;
	}
	
	protected Reader getInner() {
		return inner;
	}
	
	public void close() throws IOException {
		getInner().close();
	}
}
