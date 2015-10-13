package ast;

public class StaticVarAccessExpr extends Expr
{

	public StaticVarAccessExpr(KraClass calledClass, InstanceVariable staticInstVar)
	{
		cClass = calledClass;
		staticVar = staticInstVar;
	}
	
	KraClass cClass;
	InstanceVariable staticVar;

	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
	}

	@Override
	public Type getType()
	{
		return staticVar.getType();
	}
}
