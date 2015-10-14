/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


abstract class MessageSend  extends Expr  {
	
	public MessageSend(Method message, ExprList params)
	{
		m = message;
		exprList = params;
	}
	
	protected Method m;
	protected ExprList exprList;
}

