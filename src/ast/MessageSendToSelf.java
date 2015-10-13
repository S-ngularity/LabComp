/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public class MessageSendToSelf extends MessageSend {
	
	public MessageSendToSelf(KraClass myself, Method message, ExprList args)
	{
		super(message, args);
		self = myself;
	}
    
    public Type getType() { 
         return super.m.getType();
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
    }
    
    KraClass self;
}