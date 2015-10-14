/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendToClass extends MessageSend { 
	
	public MessageSendToClass(KraClass targetClass, Method message, ExprList args)
	{
		super(message, args);
		tClass = targetClass;
	}

	@Override
    public Type getType() { 
        return super.m.getType();
    }

	@Override
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }
    
	KraClass tClass;
}