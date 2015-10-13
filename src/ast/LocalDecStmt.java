package ast;


public class LocalDecStmt extends Statement
{
	public LocalDecStmt(Variable v)
	{
		declaredVar = v;
	}

	@Override
	public void genC(PW pw)
	{
	}
	
	Variable declaredVar;
}
