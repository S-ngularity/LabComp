/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class SelfInstVarAccessExpr extends Expr
{

	public SelfInstVarAccessExpr(KraClass calledClass, InstanceVariable staticInstVar)
	{
		cClass = calledClass;
		staticVar = staticInstVar;
	}
	
	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
		pw.print("this."+staticVar.getName());
	}

	@Override
	public Type getType()
	{
		return staticVar.getType();
	}
	
	public Variable getVariable()
	{
		return staticVar;
	}
	
	KraClass cClass;
	InstanceVariable staticVar;
}
