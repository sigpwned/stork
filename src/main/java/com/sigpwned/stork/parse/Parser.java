package com.sigpwned.stork.parse;

import java.io.IOException;
import java.text.ParseException;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;

public class Parser {
	private Tokenizer tokens;
	
	public Parser(Tokenizer tokens) {
		this.tokens = tokens;
	}
	
	public Tokenizer getTokens() {
		return tokens;
	}
	
	public ExprAST expr() throws IOException, ParseException {
		return expr1();
	}
	
	protected ExprAST expr1() throws IOException, ParseException {
		ExprAST result=expr2();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.PLUS) {
				getTokens().consumeType(Token.Type.PLUS);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.PLUS, result, expr2());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.MINUS) {
				getTokens().consumeType(Token.Type.MINUS);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.MINUS, result, expr2());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected ExprAST expr2() throws IOException, ParseException {
		ExprAST result=expr3();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.STAR) {
				getTokens().consumeType(Token.Type.STAR);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.TIMES, result, expr3());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.SLASH) {
				getTokens().consumeType(Token.Type.SLASH);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.DIVIDE, result, expr3());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.PERCENT) {
				getTokens().consumeType(Token.Type.PERCENT);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.MOD, result, expr3());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected ExprAST expr3() throws IOException, ParseException {
		ExprAST result;
		
		if(getTokens().peekType() == Token.Type.MINUS) {
			getTokens().consumeType(Token.Type.MINUS);
			result = new UnaryOperatorExprAST(UnaryOperatorExprAST.Operator.NEGATIVE, expr3());
		} else
		if(getTokens().peekType() == Token.Type.PLUS) {
			getTokens().consumeType(Token.Type.PLUS);
			result = new UnaryOperatorExprAST(UnaryOperatorExprAST.Operator.POSITIVE, expr3());
		}
		else
			result = expr4();
		
		return result;
	}
	
	public ExprAST expr4() throws IOException, ParseException {
		return value();
	}
	
	protected ExprAST value() throws IOException, ParseException {
		ExprAST result;
		
		if(getTokens().peekType() == Token.Type.INT)
			result = new IntExprAST(Long.parseLong(tokens.nextToken().getText()));
		else
		if(getTokens().peekType() == Token.Type.FLOAT)
			result = new FloatExprAST(Double.parseDouble(tokens.nextToken().getText()));
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