/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
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
	public void genC(PW pw, boolean putParenthesis)
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
