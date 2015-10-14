/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendToStaticVariable extends MessageSend
{

	public MessageSendToStaticVariable(KraClass accessedClass, InstanceVariable accessedStaticVar, Method message, ExprList params)
	{
		super(message, params);

		accClass = accessedClass;
		v = accessedStaticVar;
	}
	
	@Override
	public Type getType() { 
        return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }

    private InstanceVariable v;
	private KraClass accClass;
}
