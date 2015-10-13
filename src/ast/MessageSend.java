/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


abstract class MessageSend  extends Expr  {
	
	public MessageSend(Method message, ExprList args)
	{
		m = message;
		exprList = args;
	}
	
	protected Method m;
	protected ExprList exprList;
}

