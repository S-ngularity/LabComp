package ast;

public class ThisExpr extends Expr
{

	public ThisExpr(KraClass thisClass)
	{
		tClass = thisClass;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
		pw.print("this");
	}

	@Override
	public Type getType()
	{
		return tClass;
	}
	
	KraClass tClass;
}
