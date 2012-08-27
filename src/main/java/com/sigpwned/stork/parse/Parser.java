package com.sigpwned.stork.parse;

import java.io.IOException;
import java.text.ParseException;

import com.sigpwned.stork.ast.Expr;
import com.sigpwned.stork.ast.expr.BinaryOperatorExpr;
import com.sigpwned.stork.ast.expr.FloatExpr;
import com.sigpwned.stork.ast.expr.IntExpr;
import com.sigpwned.stork.ast.expr.UnaryOperatorExpr;

public class Parser {
	private Tokenizer tokens;
	
	public Parser(Tokenizer tokens) {
		this.tokens = tokens;
	}
	
	public Tokenizer getTokens() {
		return tokens;
	}
	
	public Expr expr() throws IOException, ParseException {
		return expr1();
	}
	
	protected Expr expr1() throws IOException, ParseException {
		Expr result=expr2();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.PLUS) {
				getTokens().consumeType(Token.Type.PLUS);
				result   = new BinaryOperatorExpr(BinaryOperatorExpr.Operator.PLUS, result, expr2());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.MINUS) {
				getTokens().consumeType(Token.Type.MINUS);
				result   = new BinaryOperatorExpr(BinaryOperatorExpr.Operator.MINUS, result, expr2());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected Expr expr2() throws IOException, ParseException {
		Expr result=expr3();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.STAR) {
				getTokens().consumeType(Token.Type.STAR);
				result   = new BinaryOperatorExpr(BinaryOperatorExpr.Operator.TIMES, result, expr3());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.SLASH) {
				getTokens().consumeType(Token.Type.SLASH);
				result   = new BinaryOperatorExpr(BinaryOperatorExpr.Operator.DIVIDE, result, expr3());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.PERCENT) {
				getTokens().consumeType(Token.Type.PERCENT);
				result   = new BinaryOperatorExpr(BinaryOperatorExpr.Operator.MOD, result, expr3());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected Expr expr3() throws IOException, ParseException {
		Expr result;
		
		if(getTokens().peekType() == Token.Type.MINUS) {
			getTokens().consumeType(Token.Type.MINUS);
			result = new UnaryOperatorExpr(UnaryOperatorExpr.Operator.NEGATIVE, expr3());
		} else
		if(getTokens().peekType() == Token.Type.PLUS) {
			getTokens().consumeType(Token.Type.PLUS);
			result = new UnaryOperatorExpr(UnaryOperatorExpr.Operator.POSITIVE, expr3());
		}
		else
			result = expr4();
		
		return result;
	}
	
	public Expr expr4() throws IOException, ParseException {
		return value();
	}
	
	protected Expr value() throws IOException, ParseException {
		Expr result;
		
		if(getTokens().peekType() == Token.Type.INT)
			result = new IntExpr(Long.parseLong(tokens.nextToken().getText()));
		else
		if(getTokens().peekType() == Token.Type.FLOAT)
			result = new FloatExpr(Double.parseDouble(tokens.nextToken().getText()));
		else
		if(getTokens().peekType() == Token.Type.LPAREN) {
			getTokens().consumeType(Token.Type.LPAREN);
			result = expr();
			getTokens().consumeType(Token.Type.RPAREN);
		}
		else
			throw new ParseException("Unexpected token: "+tokens.peekToken().getText(), tokens.peekToken().getOffset());
		
		return result;
	}
	
	public void close() throws IOException {
		getTokens().close();
	}
}