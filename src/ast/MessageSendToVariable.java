/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendToVariable extends MessageSend { 
	
	public MessageSendToVariable(Variable var, Method message, ExprList params)
	{
		super(message, params);
		
		v = var;
	}

	@Override
    public Type getType() { 
        return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }

    private Variable v;
}    