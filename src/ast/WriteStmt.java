/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

public class WriteStmt extends Statement{
	
	ExprList exprs;
	
	public WriteStmt(ExprList e){
		exprs = e;
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("write(");
		exprs.genKra(pw);
		pw.print(");");
	}
	
	@Override
	public void genC(PW pw) {
		
		Iterator<Expr> it = exprs.elements();
		
		while(it.hasNext())
		{
			Expr v = it.next();
			
			if(v.getType() == Type.intType)
			{
				pw.printIdent("printf(\"%d \", ");
				v.genC(pw, true);
				pw.print(");");
				
				if(it.hasNext())
					pw.println("");
			}
			
			else if(v.getType() == Type.stringType)
			{
				// IMPLEMENTAÇÃO CERTA DE ACORDO COM ESPECIFICAÇÃO DO KRAKATOA, COM WRITE NÃO PULANDO LINHA
				/*
				pw.printIdent("fputs(");
				v.genC(pw, true);
				pw.print(", stdout);");
				*/
				
				// implementação de acordo com o PDF de geração de código C
				pw.printIdent("puts(");
				v.genC(pw, true);
				pw.print(");");
				
				if(it.hasNext())
					pw.println("");
			}
		}
	}

}
