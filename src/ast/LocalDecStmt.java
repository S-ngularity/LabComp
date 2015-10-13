/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public class LocalDecStmt extends Statement
{
	public LocalDecStmt(Variable v)
	{
		declaredVar = v;
	}
	
	Variable declaredVar;

	@Override
	public void genKra(PW pw)
	{
		pw.print(declaredVar.toString());
	}
}
