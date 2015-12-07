/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

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
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print("super.");
		super.genKra(pw, false);
    }
    
	@Override
    public void genC( PW pw, boolean putParenthesis )
	{
		pw.print(m.getCname() + "((" + m.ownerClass.getCname() + "*) this");
		
		if(exprList.getSize() > 0)
			pw.print(", ");
		
		exprList.genC(pw);
		pw.print(")");
    }
	
	KraClass superclass;
}