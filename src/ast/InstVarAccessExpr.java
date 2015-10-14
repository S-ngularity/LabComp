package ast;

public class InstVarAccessExpr extends Expr
{

	public InstVarAccessExpr(KraClass calledClass, InstanceVariable staticInstVar)
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
