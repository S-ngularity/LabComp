package ast;


public class IdExpr extends Expr
{
	public IdExpr(Variable var)
	{
		v = var;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
	}

	@Override
	public Type getType()
	{
		return v.getType();
	}
	
	private Variable v;
}
