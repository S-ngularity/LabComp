/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;


public class MessageSendToVariable extends MessageSend { 
	
	public MessageSendToVariable(Variable var, Method message, ExprList args)
	{
		super(message, args);
		
		v = var;
	}

    public Type getType() { 
        return super.m.getType();
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        
    }

    private Variable v;
}    