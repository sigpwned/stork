package com.sigpwned.stork;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.parse.Parser;
import com.sigpwned.stork.engine.compilation.parse.Token;
import com.sigpwned.stork.engine.compilation.parse.Tokenizer;
import com.sigpwned.stork.x.StorkException;

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
					Parser parser=new Parser(new Tokenizer(new StringReader(line)));
					try {
						try {
							ExprAST expr=parser.expr();
							if(parser.getTokens().peekType() != Token.Type.EOF)
								System.err.println("WARNING: Ignoring tokens: "+parser.getTokens().peekToken().getText());
							expr.analyze();
							OUT.write(expr.compile().eval().toString()+"\n");
							OUT.flush();
						}
						catch(StorkException e) {
							OUT.write("ERROR: "+e.getMessage()+"\n");
						}
					}
					finally {
						parser.close();
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
