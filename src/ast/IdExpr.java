/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
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
		if ( putParenthesis )
			pw.print("(");
		
		pw.print(v.getName());
		
		if ( putParenthesis )
			pw.print(")");
	}
	
	@Override
	public void genC(PW pw, boolean putParenthesis)
	{
		if ( putParenthesis )
			pw.print("(");
		
		pw.print(v.getCname());
		
		if ( putParenthesis )
			pw.print(")");
	}

	@Override
	public Type getType()
	{
		return v.getType();
	}
	
	public Variable getVariable()
	{
		return v;
	}
	
	private Variable v;
}
