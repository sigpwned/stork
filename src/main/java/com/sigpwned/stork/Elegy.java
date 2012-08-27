package com.sigpwned.stork;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.sigpwned.stork.ast.AST;
import com.sigpwned.stork.ast.Expr;
import com.sigpwned.stork.ast.expr.BinaryOperatorExpr;
import com.sigpwned.stork.ast.expr.FloatExpr;
import com.sigpwned.stork.ast.expr.IntExpr;
import com.sigpwned.stork.ast.expr.UnaryOperatorExpr;
import com.sigpwned.stork.parse.Parser;
import com.sigpwned.stork.parse.Token;
import com.sigpwned.stork.parse.Tokenizer;

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
						Expr expr=parser.expr();
						if(parser.getTokens().peekType() != Token.Type.EOF)
							System.err.println("WARNING: Ignoring tokens: "+parser.getTokens().peekToken().getText());
						print(expr, new ArrayList<Expr>(), new IdentityHashMap<Expr,Integer>());
						OUT.flush();
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
	
	public static void print(Expr expr, List<Expr> emitting, Map<Expr,Integer> counts) throws IOException {
		String node;
		if(expr instanceof BinaryOperatorExpr)
			node = expr.asBinaryOperator().getOperator().getText();
		else
		if(expr instanceof UnaryOperatorExpr)
			node = expr.asUnaryOperator().getOperator().getText();
		else
		if(expr instanceof IntExpr)
			node = Long.toString(expr.asInt().getValue());
		else
		if(expr instanceof FloatExpr)
			node = Double.toString(expr.asFloat().getValue());
		else
			node = "???????";
		
		String line="    ";
		for(int i=0;i<emitting.size();i++) {
			Expr ancestor=emitting.get(i);
			if(get(counts, ancestor, 0) < ancestor.getChildren().size()) {
				if(i == emitting.size()-1)
					line = line+"|-- ";
				else
					line = line+"|   ";
			}
			else
				line = line+"    ";
		}
		line = line+node;
		line = line+"\n";
		OUT.write(line);
		
		if(emitting.size() != 0)
			inc(counts, emitting.get(emitting.size()-1));
		
		emitting.add(expr);
		for(AST child : expr.getChildren())
			print((Expr) child, emitting, counts);
		emitting.remove(expr);
	}
	
	public static <K,V> V get(Map<K,V> map, K k, V def) {
		V result=map.get(k);
		if(result == null)
			result = def;
		return result;
	}
	
	public static <K> void inc(Map<K,Integer> map, K k) {
		Integer count=map.get(k);
		if(count == null)
			count = 0;
		map.put(k, count+1);
	}
	
	public static String times(String text, int times) {
		StringBuilder result=new StringBuilder();
		for(int i=0;i<times;i++)
			result.append(text);
		return result.toString();
	}
}
