package com.sigpwned.stork;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

import com.sigpwned.stork.engine.compilation.Translator;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.parse.Parser;
import com.sigpwned.stork.engine.compilation.parse.Token;
import com.sigpwned.stork.engine.compilation.parse.Tokenizer;
import com.sigpwned.stork.engine.runtime.Scope;
import com.sigpwned.stork.x.InternalStorkException;
import com.sigpwned.stork.x.StorkException;

public class Stork {
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
				Scope scope=new Scope();
				Translator compiler=new Translator();
				for(String line=line();line!=null;line=line()) {
					Tokenizer tokens=new Tokenizer(new StringReader(line));
					try {
						Parser parser=new Parser(tokens);
						try {
							StmtAST stmt=parser.stmt();
							if(parser.getTokens().peekType() != Token.Type.EOF)
								System.err.println("WARNING: Ignoring tokens: "+parser.getTokens().peekToken().getText());
							Object value=stmt.translate(compiler).exec(scope);
							if(value != null)
								OUT.write(value.toString()+"\n");
							OUT.flush();
						}
						catch(InternalStorkException e) {
							OUT.write("INTERNAL ERROR: "+e.getMessage()+"\n");
							e.printStackTrace();
						}
						catch(StorkException e) {
							OUT.write("ERROR: "+e.getMessage()+"\n");
						}
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
