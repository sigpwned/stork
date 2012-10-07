package com.sigpwned.stork.engine.compilation.parse;

import java.io.IOException;
import java.text.ParseException;

import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.CastExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.VarExprAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.DeclareStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.EvalStmtAST;

public class Parser {
	private Tokenizer tokens;
	
	public Parser(Tokenizer tokens) {
		this.tokens = tokens;
	}
	
	public Tokenizer getTokens() {
		return tokens;
	}
	
	public StmtAST stmt() throws IOException, ParseException {
		StmtAST result;
		
		if(getTokens().peekType() == Token.Type.VAR) {
			getTokens().consumeType(Token.Type.VAR);
			String name=getTokens().consumeType(Token.Type.SYMBOL).getText();
			getTokens().consumeType(Token.Type.COLON);
			TypeExpr type=type();
			
			ExprAST init;
			if(getTokens().peekType() == Token.Type.EQ) {
				getTokens().consumeType(Token.Type.EQ);
				init = expr();
			}
			else
				init = null;
			
			term();
			
			result = new DeclareStmtAST(name, type, init);
		}
		else {
			ExprAST expr=expr();
			term();
			result = new EvalStmtAST(expr);
		}
		
		return result;
	}
	
	protected void term() throws IOException, ParseException {
		if(getTokens().peekType() == Token.Type.EOF) {
			// EOF qualifies as a statement terminator
		} else
		if(getTokens().peekType() == Token.Type.SEMICOLON) {
			// Semicolons are explicit terminators; consume it.
			getTokens().consumeType(Token.Type.SEMICOLON);
		} else
		if(getTokens().peekToken().hasFlag(Token.Flag.NEWLINE)) {
			// The next token has a newline before it. That's a term.
		}
		else {
			// We need a term and we didn't get one. Just go ahead
			// and consume a `;', which will generate an error since
			// we know the next token is not a `;'.
			getTokens().consumeType(Token.Type.SEMICOLON);
		}
	}
	
	protected TypeExpr type() throws IOException, ParseException {
		return new TypeExpr(getTokens().consumeType(Token.Type.SYMBOL).getText());
	}
	
	protected ExprAST expr() throws IOException, ParseException {
		return expr1();
	}
	
	protected ExprAST expr1() throws IOException, ParseException {
		ExprAST result=expr2();
		
		if(getTokens().peekType() == Token.Type.EQ) {
			getTokens().consumeType(Token.Type.EQ);
			result = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.EQ, result, expr1());
		}
		
		return result;
	}
	
	protected ExprAST expr2() throws IOException, ParseException {
		ExprAST result=expr3();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.PLUS) {
				getTokens().consumeType(Token.Type.PLUS);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.PLUS, result, expr3());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.MINUS) {
				getTokens().consumeType(Token.Type.MINUS);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.MINUS, result, expr3());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected ExprAST expr3() throws IOException, ParseException {
		ExprAST result=expr4();
		
		boolean replaced;
		do {
			replaced = false;
			if(getTokens().peekType() == Token.Type.STAR) {
				getTokens().consumeType(Token.Type.STAR);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.TIMES, result, expr4());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.SLASH) {
				getTokens().consumeType(Token.Type.SLASH);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.DIVIDE, result, expr4());
				replaced = true;
			} else
			if(getTokens().peekType() == Token.Type.PERCENT) {
				getTokens().consumeType(Token.Type.PERCENT);
				result   = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.MOD, result, expr4());
				replaced = true;
			}
		} while(replaced);
		
		return result;
	}

	protected ExprAST expr4() throws IOException, ParseException {
		ExprAST result;
		
		if(getTokens().peekType() == Token.Type.MINUS) {
			getTokens().consumeType(Token.Type.MINUS);
			result = new UnaryOperatorExprAST(UnaryOperatorExprAST.Operator.NEGATIVE, expr4());
		} else
		if(getTokens().peekType() == Token.Type.PLUS) {
			getTokens().consumeType(Token.Type.PLUS);
			result = new UnaryOperatorExprAST(UnaryOperatorExprAST.Operator.POSITIVE, expr4());
		}
		else
			result = expr5();
		
		return result;
	}
	
	public ExprAST expr5() throws IOException, ParseException {
		return value();
	}
	
	protected ExprAST value() throws IOException, ParseException {
		ExprAST result;
		
		if(getTokens().peekType() == Token.Type.INT)
			result = new IntExprAST(Long.parseLong(getTokens().nextToken().getText()));
		else
		if(getTokens().peekType() == Token.Type.FLOAT)
			result = new FloatExprAST(Double.parseDouble(getTokens().nextToken().getText()));
		else
		if(getTokens().peekType() == Token.Type.SYMBOL)
			result = new VarExprAST(getTokens().nextToken().getText());
		else
		if(getTokens().peekType() == Token.Type.LPAREN) {
			getTokens().consumeType(Token.Type.LPAREN);
			if(getTokens().peekType() == Token.Type.CAST) {
				getTokens().consumeType(Token.Type.CAST);
				TypeExpr type=type();
				getTokens().consumeType(Token.Type.RPAREN);
				ExprAST expr=expr();
				result = new CastExprAST(type, expr);
			}
			else {
				result = expr();
				getTokens().consumeType(Token.Type.RPAREN);
			}
		}
		else
			throw new ParseException("Unexpected token: "+getTokens().peekToken().getText(), getTokens().peekToken().getOffset());
		
		return result;
	}
	
	public void close() throws IOException {
		getTokens().close();
	}
}