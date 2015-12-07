/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

public class WritelnStmt extends Statement{
	
	ExprList exprs;
	
	public WritelnStmt(ExprList e){
		exprs = e;
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("writeln(");
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
				// SEM ESPAÇO depois do int
				//pw.printIdent("printf(\"%d\", ");
				
				// COM ESPAÇO depois do int
				pw.printIdent("printf(\"%d \", ");
				
				v.genC(pw, true);
				pw.print(");");
				pw.println("");
			}
			
			else if(v.getType() == Type.stringType)
			{
				// IMPLEMENTAÇÃO MAIS COERENTE
				pw.printIdent("fputs(");
				v.genC(pw, true);
				pw.print(", stdout);");
				
				// implementação de acordo com o PDF de geração de código C
				/*
				pw.printIdent("puts(");
				v.genC(pw, true);
				pw.print(");");
				*/
				
				pw.println("");
			}
		}
		
		pw.printlnIdent("printf(\"\\n\");");
	}
	
}
