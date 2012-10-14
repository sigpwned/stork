package com.sigpwned.stork.engine.compilation.parse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.sigpwned.stork.engine.compilation.ast.BlockAST;
import com.sigpwned.stork.engine.compilation.ast.ExprAST;
import com.sigpwned.stork.engine.compilation.ast.ParameterAST;
import com.sigpwned.stork.engine.compilation.ast.StmtAST;
import com.sigpwned.stork.engine.compilation.ast.TypeExpr;
import com.sigpwned.stork.engine.compilation.ast.expr.BinaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.CastExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.FloatExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.IntExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.InvokeExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.LambdaExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.UnaryOperatorExprAST;
import com.sigpwned.stork.engine.compilation.ast.expr.VarExprAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.DeclareStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.EvalStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.FunctionStmtAST;
import com.sigpwned.stork.engine.compilation.ast.stmt.ReturnStmtAST;
import com.sigpwned.stork.engine.compilation.ast.type.FunctionTypeExpr;
import com.sigpwned.stork.engine.compilation.ast.type.SymbolTypeExpr;

public class Parser {
	private Tokenizer tokens;
	
	public Parser(Tokenizer tokens) {
		this.tokens = tokens;
	}
	
	public Tokenizer getTokens() {
		return tokens;
	}
	
	public BlockAST block() throws IOException, ParseException {
		BlockAST result=new BlockAST();

		while(getTokens().peekType()!=Token.Type.EOF && getTokens().peekType()!=Token.Type.END)
			result.addBody(stmt());

		return result;
	}
	
	public StmtAST stmt() throws IOException, ParseException {
		StmtAST result;
		
		if(getTokens().peekType() == Token.Type.VAR) {
			getTokens().consumeType(Token.Type.VAR);
			String name=getTokens().consumeType(Token.Type.SYMBOL).getText();
			getTokens().consumeType(Token.Type.COLON);
			TypeExpr type=type();
			
			ExprAST init;
			if(!sterm() && getTokens().peekType()==Token.Type.EQ) {
				getTokens().consumeType(Token.Type.EQ);
				init = expr();
			}
			else
				init = null;
			
			term();
			
			result = new DeclareStmtAST(name, type, init);
		} else
		if(getTokens().peekType() == Token.Type.FUN) {
			getTokens().consumeType(Token.Type.FUN);
			TypeExpr resultType=type();
			String name=getTokens().consumeType(Token.Type.SYMBOL).getText();
			getTokens().consumeType(Token.Type.LPAREN);
			List<ParameterAST> parameters=params();
			getTokens().consumeType(Token.Type.RPAREN);
			BlockAST body=block();
			getTokens().consumeType(Token.Type.END);
			result = new FunctionStmtAST(resultType, name, parameters, body);
		} else
		if(getTokens().peekType() == Token.Type.RETURN) {
			getTokens().consumeType(Token.Type.RETURN);
			ExprAST expr=expr();
			result = new ReturnStmtAST(expr);
		}
		else {
			ExprAST expr=expr();
			term();
			result = new EvalStmtAST(expr);
		}
		
		return result;
	}
	
	protected List<ParameterAST> params() throws IOException, ParseException {
		List<ParameterAST> result=new ArrayList<ParameterAST>();
		if(getTokens().peekType() != Token.Type.RPAREN) {
			result.add(param());
			while(getTokens().peekType() == Token.Type.COMMA) {
				getTokens().consumeType(Token.Type.COMMA);
				result.add(param());
			}
		}
		return result;
	}
	
	protected ParameterAST param() throws IOException, ParseException {
		String name=getTokens().consumeType(Token.Type.SYMBOL).getText();
		getTokens().consumeType(Token.Type.COLON);
		TypeExpr type=type();
		return new ParameterAST(type, name);
	}
	
	/**
	 * "Soft terminator." Used by the parser as a hint that the current
	 * expression has ended. This logic is required to make the REPL work
	 * properly in the presence of multi-line constructs.
	 * 
	 * @return <code>true</code> if there exists a "soft terminator" before
	 *         the next token; <code>false</code> otherwise.
	 */
	protected boolean sterm() {
		return getTokens().lastToken().hasFlag(Token.Flag.POSTNEWLINE);
	}
	
	protected void term() throws IOException, ParseException {
		if(sterm()) {
			// The previous token had a newline after it. That's a term.
		} else
		if(getTokens().peekType() == Token.Type.EOF) {
			// EOF qualifies as a statement terminator
		} else
		if(getTokens().peekType() == Token.Type.SEMICOLON) {
			// Semicolons are explicit terminators; consume it.
			getTokens().consumeType(Token.Type.SEMICOLON);
		}
		else {
			// We need a term and we didn't get one. Just go ahead
			// and consume a `;', which will generate an error since
			// we know the next token is not a `;'.
			getTokens().consumeType(Token.Type.SEMICOLON);
		}
	}
	
	protected TypeExpr type() throws IOException, ParseException {
		TypeExpr result;
		
		if(getTokens().peekType() == Token.Type.LPAREN) {
			getTokens().consumeType(Token.Type.LPAREN);
			
			List<TypeExpr> parameterTypes=new ArrayList<TypeExpr>();
			if(getTokens().peekType() != Token.Type.RPAREN) {
				parameterTypes.add(type());
				while(getTokens().peekType() == Token.Type.COMMA) {
					getTokens().consumeType(Token.Type.COMMA);
					parameterTypes.add(type());
				}
			}
			
			getTokens().consumeType(Token.Type.RPAREN);
			
			getTokens().consumeType(Token.Type.ARROW);
			
			TypeExpr resultType=type();
			
			result = new FunctionTypeExpr(resultType, parameterTypes);
		}
		else
			result = new SymbolTypeExpr(getTokens().consumeType(Token.Type.SYMBOL).getText());
		
		return result;
	}
	
	protected ExprAST expr() throws IOException, ParseException {
		return expr1();
	}
	
	protected ExprAST expr1() throws IOException, ParseException {
		ExprAST result=expr2();
		
		if(sterm()) {
			// There is a soft terminator. Consider this expr over.
		} else
		if(getTokens().peekType()==Token.Type.EQ) {
			getTokens().consumeType(Token.Type.EQ);
			result = new BinaryOperatorExprAST(BinaryOperatorExprAST.Operator.EQ, result, expr1());
		}
		
		return result;
	}
	
	protected ExprAST expr2() throws IOException, ParseException {
		ExprAST result=expr3();
		
		if(!sterm()) {
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
			} while(replaced && !sterm());
		}
		
		return result;
	}

	protected ExprAST expr3() throws IOException, ParseException {
		ExprAST result=expr4();
		
		if(!sterm()) {
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
			} while(replaced && !sterm());
		}
		
		return result;
	}

	protected ExprAST expr4() throws IOException, ParseException {
		ExprAST result;
		
		if(!sterm() && getTokens().peekType() == Token.Type.MINUS) {
			getTokens().consumeType(Token.Type.MINUS);
			result = new UnaryOperatorExprAST(UnaryOperatorExprAST.Operator.NEGATIVE, expr4());
		} else
		if(!sterm() && getTokens().peekType() == Token.Type.PLUS) {
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
		if(getTokens().peekType() == Token.Type.LAMBDA) {
			getTokens().consumeType(Token.Type.LAMBDA);
			getTokens().consumeType(Token.Type.LPAREN);
			List<ParameterAST> parameters=params();
			getTokens().consumeType(Token.Type.RPAREN);
			getTokens().consumeType(Token.Type.ARROW);
			ExprAST body=expr();
			result = new LambdaExprAST(parameters, body);
		} else
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
		
		while(!sterm() && getTokens().peekType()==Token.Type.LPAREN) {
			InvokeExprAST invoke=new InvokeExprAST(result);
			getTokens().consumeType(Token.Type.LPAREN);
			if(getTokens().peekType() != Token.Type.RPAREN) {
				invoke.addArgument(expr());
				while(getTokens().peekType() == Token.Type.COMMA) {
					getTokens().consumeType(Token.Type.COMMA);
					invoke.addArgument(expr());
				}
			}
			getTokens().consumeType(Token.Type.RPAREN);
			result = invoke;
		}
		
		return result;
	}
	
	public void close() throws IOException {
		getTokens().close();
	}
}