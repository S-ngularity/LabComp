/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public abstract class MessageSend  extends Expr  {
	
	public MessageSend(Method message, ExprList params)
	{
		m = message;
		exprList = params;
	}
	
	@Override
	public void genKra(PW pw, boolean putParenthesis)
	{
		pw.print(m.getName()+"(");
		exprList.genKra(pw);
		pw.print(")");
	}
	
	protected Method m;
	protected ExprList exprList;
}

