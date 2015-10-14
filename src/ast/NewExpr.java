/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public class NewExpr extends Expr
{
	public NewExpr(KraClass classType)
	{
		cType = classType;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
		pw.print("new "+getType().getName()+"()");
	}

	@Override
	public Type getType()
	{
		return cType;
	}
	
	private KraClass cType;
}
