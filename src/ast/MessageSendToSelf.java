/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public class MessageSendToSelf extends MessageSend {
	
	public MessageSendToSelf(KraClass myself, Method message, ExprList params)
	{
		super(message, params);
		self = myself;
	}
    
	@Override
    public Type getType() { 
         return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print("this.");
		super.genKra(pw, false);
    }
    
    KraClass self;
}