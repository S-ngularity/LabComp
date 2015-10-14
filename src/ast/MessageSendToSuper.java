/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendToSuper extends MessageSend { 
	
	public MessageSendToSuper(KraClass actualSuperclass, Method message, ExprList params)
	{
		super(message, params);
		superclass = actualSuperclass;
	}

	@Override
    public Type getType() { 
        return super.m.getType();
    }

	@Override
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }
    
	KraClass superclass;
}