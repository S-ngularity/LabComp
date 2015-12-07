/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

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
		
		if(exprList.getSize() > 0)
		{
			Iterator<Variable> itParams = m.getParamList().elements();
			Iterator<Expr> itExprs = exprList.elements();
			while(itParams.hasNext())
			{
				Variable v = (Variable) itParams.next();
				Expr e = (Expr) itExprs.next();

				if(v.getType() != e.getType() && v.getType() != Type.nullType)
					pw.print("(" + v.getType().getCname() + ") ");

				e.genC(pw, false);

				if(itParams.hasNext())
					pw.print(", ");
			}
		}
		//exprList.genC(pw);
		
		pw.print(")");
    }
    
	KraClass tClass;
}