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
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print(tClass.getName()+".");
		super.genKra(pw, false);
    }
	
	@Override
    public void genC( PW pw, boolean putParenthesis )
	{
		pw.print(m.getCname()+"(");
		exprList.genC(pw);
		pw.print(")");
    }
    
	KraClass tClass;
}