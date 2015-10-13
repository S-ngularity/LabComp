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
	}

	@Override
	public Type getType()
	{
		return cType;
	}
	
	private KraClass cType;
}
