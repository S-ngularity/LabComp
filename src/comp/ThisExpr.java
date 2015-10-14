package comp;

import ast.Expr;
import ast.KraClass;
import ast.PW;
import ast.Type;

class ThisExpr extends Expr
{

	public ThisExpr(KraClass thisClass)
	{
		tClass = thisClass;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
	}

	@Override
	public Type getType()
	{
		return tClass;
	}
	
	KraClass tClass;
}
