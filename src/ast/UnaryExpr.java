/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/

package ast;

import lexer.*;

public class UnaryExpr extends Expr {

	public UnaryExpr(Expr expr, Symbol op) {
		this.expr = expr;
		this.op = op;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		switch (op) {
		case PLUS:
			pw.print("+");
			break;
		case MINUS:
			pw.print("-");
			break;
		case NOT:
			pw.print("!");
			break;
		default:
			pw.print(" internal error at UnaryExpr::genC");

		}
		expr.genKra(pw, false);
	}

	@Override
	public Type getType() {
		return expr.getType();
	}

	private Expr	expr;
	private Symbol	op;
}
