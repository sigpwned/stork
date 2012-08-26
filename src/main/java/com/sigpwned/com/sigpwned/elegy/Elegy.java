package com.sigpwned.com.sigpwned.elegy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import com.sigpwned.com.sigpwned.elegy.parse.Token;
import com.sigpwned.com.sigpwned.elegy.parse.Tokenizer;

public class Elegy {
	public static Reader IN;
	public static Writer OUT;
	
	public static void main(String[] args) {
		try {
			if(System.console() != null) {
				IN  = System.console().reader();
				OUT = System.console().writer();
			}
			else {
				IN  = new InputStreamReader(System.in);
				OUT = new OutputStreamWriter(System.out);
			}
			try {
				for(String line=line();line!=null;line=line()) {
					Tokenizer tokens=new Tokenizer(new StringReader(line));
					try {
						for(Token token=tokens.nextToken();token.getType()!=Token.Type.EOF;token=tokens.nextToken())
							OUT.write(String.format("    %-6s  %s\n", token.getType().name(), token.getText()));
						OUT.flush();
					}
					finally {
						tokens.close();
					}
				}
			}
			finally {
				IN.close();
				OUT.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String line() throws IOException {
		String result;
		
		OUT.write(">>> "); OUT.flush();
		
		int ch=IN.read();
		if(ch != -1) {
			StringBuilder buf=new StringBuilder();
			buf.append((char) ch);
			for(ch=IN.read();ch!=-1;ch=IN.read())
				if(ch != '\n')
					buf.append((char) ch);
				else
					break;
			result = buf.toString().trim();
		}
		else
			result = null;
		
		return result;
	}
}
