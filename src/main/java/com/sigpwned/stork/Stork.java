package com.sigpwned.stork;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
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
	
	public static class LineOrientedReader extends Reader {
		private class Line {
			private String buffer;
			
			public Line() {
			}
			
			public int read() throws IOException {
				int result;
				
				if(buffer == null)
					buffer = line();
				
				if(buffer != null) {
					result = buffer.charAt(0);
					buffer  = buffer.substring(1, buffer.length());
					if(buffer.length() == 0)
						buffer = null;
				}
				else
					result = -1;
				
				return result;
			}
			
			private String line() throws IOException {
				String result;
				
				outer.write(prompt); outer.flush();
				
				int ch=inner.read();
				if(ch != -1) {
					StringBuilder buf=new StringBuilder();
					buf.append((char) ch);
					for(ch=inner.read();ch!=-1;ch=inner.read()) {
						buf.append((char) ch);
						if(ch == '\n')
							break;
					}
					result = buf.toString();
				}
				else
					result = null;
				
				return result;				
			}
		}
		
		private Reader inner;
		private Writer outer;
		private String prompt;
		private Line line;
		
		public LineOrientedReader(Reader inner, Writer outer, String prompt) {
			this.inner  = inner;
			this.outer  = outer;
			this.prompt = prompt;
			this.line   = new Line();
		}
		
		protected Reader getInner() {
			return inner;
		}
		
		public String getPrompt() {
			return prompt;
		}
		
		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}

		public int read(char[] array, int offset, int length) throws IOException {
			int result=0;
			
			for(int i=0;i<length;i++) {
				int ch=read();
				if(ch != -1)
					array[offset+i] = (char) ch;
				else
					break;
				result = result+1;
			}
			
			return result;
		}
		
		public int read() throws IOException {
			int result;
			if(line != null) {
				result = line.read();
				if(result == -1)
					line = null;
			}
			else
				result = -1;
			return result;
		}

		public void close() throws IOException {
			inner.close();
			outer.close();
		}
	}
	
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
			
			LineOrientedReader reader=new LineOrientedReader(IN, OUT, ">>> ");
			Scope scope=new Scope(null);
			Translator compiler=new Translator();
			Tokenizer tokens=new Tokenizer(reader);
			try {
				Parser parser=new Parser(tokens);
				while(tokens.peekType() != Token.Type.EOF) {
					reader.setPrompt("... ");
					StmtAST stmt=parser.stmt();
					
					try {
						stmt.defineFunctions(compiler.getGlobe(), compiler);
						Object value=stmt.translate(compiler.getGlobe(), compiler).exec(scope);
						if(value != null)
							OUT.write(value.toString()+"\n");
					}
					catch(InternalStorkException e) {
						OUT.write("INTERNAL ERROR: "+e.getMessage()+"\n");
						e.printStackTrace();
					}
					catch(StorkException e) {
						OUT.write("ERROR: "+e.getMessage()+"\n");
					}
					
					OUT.flush();
					reader.setPrompt(">>> ");
				}
			}
			finally {
				tokens.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
