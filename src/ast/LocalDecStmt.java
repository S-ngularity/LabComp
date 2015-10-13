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
	}
}
