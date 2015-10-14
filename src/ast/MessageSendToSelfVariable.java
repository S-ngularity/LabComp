/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendToSelfVariable extends MessageSend
{

	public MessageSendToSelfVariable(KraClass myself, InstanceVariable myAccessedVar, Method message, ExprList params)
	{
		super(message, params);
		self = myself;
		accessedVar = myAccessedVar;
	}

	@Override
    public Type getType() { 
         return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print("this."+accessedVar.getName()+".");
		super.genKra(pw, false);
    }
    
    KraClass self;
	InstanceVariable accessedVar;
}
