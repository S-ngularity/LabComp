/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

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
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print(v.getName()+".");
		super.genKra(pw, false);
    }
	
//	@Override
//    public void genC( PW pw, boolean putParenthesis )
//	{
//		if(v.getType() == m.ownerClass && m.ownerClass.searchPrivateMethod(m.getName()) != null)
//		{
//			pw.print(m.getCname() + "(" + m.ownerClass.getCname() + ") this");
//		
//			if(exprList.getSize() > 0)
//				pw.print(", ");
//
//			exprList.genC(pw);
//			pw.print(")");
//		}
//		
//		else
//		{
//			//( (int (*)(_class_A *)) this->vt[0]) ( (_class_A *) this )
//
//			pw.print("( ("+ m.getType().getCname() +" (*)(");
//
//			/* CLASSE A SER PASSADA NÃO NECESSARIAMENTE É A DA SELF, CASO FUNÇÃO SEJA DE SUPERCLASSE */
//			pw.print(m.ownerClass.getCname() + "*");
//
//			if(m.getParamList().getSize() > 0)
//			{
//				Iterator<Variable> it = m.getParamList().elements();
//				while(it.hasNext())
//				{
//					Variable v = (Variable) it.next();
//
//					pw.print(", ");
//					pw.print(v.getType().getCname());
//				}
//			}
//
//			pw.print(")) this->vt[" + self.getCMethodIndex(m.getName()) + "]) ");
//
//			pw.print("( (" + m.ownerClass.getCname() + "*) this");
//			
//			if(exprList.getSize() > 0)
//				pw.print(", ");
//			
//			exprList.genC(pw);
//			pw.print(")");
//		}
//    }

    private Variable v;
}    