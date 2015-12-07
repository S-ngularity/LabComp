/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

public class MessageSendToVariable extends MessageSend { 
	
	public MessageSendToVariable(Variable v, Method message, ExprList params)
	{
		super(message, params);
		
		calledVar = v;
	}

	@Override
    public Type getType() { 
        return super.m.getType();
    }
    
	@Override
    public void genKra( PW pw, boolean putParenthesis )
	{
		pw.print(calledVar.getName()+".");
		super.genKra(pw, false);
    }
	
	@Override
    public void genC( PW pw, boolean putParenthesis )
	{
		// se m é função declarada na classe da variável v
		// e m é método privado da classe
		// então a chamada pra função privada da variável só pode estar acontecendo dentro da
		// própria classe da variável (e da função, que é a mesma) - por causa da semântica do comp
		// que não permite chamar funções privadas de fora da classe que a declara 
		if(calledVar.getType() == m.ownerClass && m.ownerClass.searchPrivateMethod(m.getName()) != null)
		{
			pw.print(m.getCname() + "(" + calledVar.getCname());
		
			if(exprList.getSize() > 0)
			{
				pw.print(", ");
			
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

			pw.print(")) " + calledVar.getCname() + "->vt[" + m.ownerClass.getCMethodIndex(m.getName()) + "]) ");

			pw.print("( ");
			if(calledVar.getType() != m.ownerClass)
				pw.print("(" + m.ownerClass.getCname() + ") ");
			pw.print(calledVar.getCname());
			
			if(exprList.getSize() > 0)
			{
				pw.print(", ");
			
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
    }

    private Variable calledVar;
}    