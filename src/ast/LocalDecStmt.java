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
	
	@Override
	public void genKra(PW pw)
	{
		pw.printIdent(declaredVar.getType().getName()+" "+declaredVar.getName()+";");
	}
	
	@Override
	public void genC(PW pw)
	{
		pw.printIdent(declaredVar.getType().getCname()+" "+declaredVar.getCname()+";");
	}
	
	Variable declaredVar;
}
