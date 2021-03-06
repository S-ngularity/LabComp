/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class StaticInstVarAccessExpr extends Expr
{

	public StaticInstVarAccessExpr(KraClass calledClass, InstanceVariable staticInstVar)
	{
		cClass = calledClass;
		staticVar = staticInstVar;
	}
	
	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
		pw.print(cClass.getName()+"."+staticVar.getName());
	}
	
	@Override
	public void genC(PW pw, boolean putParenthesis)
	{
		pw.print("_static_"+ cClass.getName() + "_" + staticVar.getName());
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
