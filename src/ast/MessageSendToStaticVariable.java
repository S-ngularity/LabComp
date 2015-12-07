/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

public class MessageSendToStaticVariable extends MessageSend
{

	public MessageSendToStaticVariable(KraClass accessedClass, InstanceVariable accessedStaticVar, Method message, ExprList params)
	{
		super(message, params);

		this.accessedClass = accessedClass;
		this.accessedStaticVar = accessedStaticVar;
	}
	
	@Override
	public Type getType() { 
        return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print(accessedClass.getName()+"."+accessedStaticVar.getName()+".");
		super.genKra(pw, false);
    }
	
	@Override
    public void genC( PW pw, boolean putParenthesis )
	{
		pw.print(m.getCname() + "(" + "_static_"+ accessedClass.getName() + "_" + accessedStaticVar.getName());

		if(exprList.getSize() > 0)
			pw.print(", ");

		exprList.genC(pw);
		pw.print(")");
    }

    private InstanceVariable accessedStaticVar;
	private KraClass accessedClass;
}
