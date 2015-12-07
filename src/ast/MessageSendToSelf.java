/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;


public class MessageSendToSelf extends MessageSend {
	
	public MessageSendToSelf(KraClass myself, Method message, ExprList params)
	{
		super(message, params);
		self = myself;
	}
    
	@Override
    public Type getType() { 
         return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print("this.");
		super.genKra(pw, false);
    }
	
	@Override
    public void genC( PW pw, boolean putParenthesis )
	{
		if(self.searchPrivateMethod(m.getName()) != null)
		{
			pw.print(m.getCname() + "(this");
		
			if(exprList.getSize() > 0)
				pw.print(", ");

			exprList.genC(pw);
			pw.print(")");
		}
		
		else
		{
			//( (int (*)(_class_A *, ...)) this->vt[0]) ( (_class_A *) this, ... )

			pw.print("( ("+ m.getType().getCname() +" (*)(");

			/* CLASSE A SER PASSADA NÃO NECESSARIAMENTE É A DA SELF, CASO FUNÇÃO SEJA DE SUPERCLASSE */
			pw.print(m.ownerClass.getCname());

			if(m.getParamList().getSize() > 0)
			{
				Iterator<Variable> it = m.getParamList().elements();
				while(it.hasNext())
				{
					Variable v = (Variable) it.next();

					pw.print(", ");
					pw.print(v.getType().getCname());
				}
			}

			pw.print(")) this->vt[" + self.getCMethodIndex(m.getName()) + "]) ");

			pw.print("( (" + m.ownerClass.getCname() + ") this");
			
			if(exprList.getSize() > 0)
				pw.print(", ");
			
			exprList.genC(pw);
			pw.print(")");
		}
    }
    
    KraClass self;
}