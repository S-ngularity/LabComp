/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class ReadStmt extends Statement{
	
	ExprList varsToRead;
	
	public ReadStmt(ExprList valuesToRead){
		varsToRead = valuesToRead;
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("read (");
		
		varsToRead.genKra(pw);
		
		pw.print(");");
	}
	
	@Override
	public void genC(PW pw) {
		
		Iterator<Expr> it = varsToRead.elements();
		
		while(it.hasNext())
		{
			Expr v = it.next();
			
			if(v.getType() == Type.intType)
			{
				pw.printlnIdent("{");
				pw.add();
				pw.printlnIdent("char __s[512];");
				pw.printlnIdent("gets(__s);");
				pw.printIdent("sscanf(__s, \"%d\", &(");
				v.genC(pw, false);
				pw.println("));");
				pw.sub();
				pw.printlnIdent("}");
			}
			
			else if(v.getType() == Type.stringType)
			{
				pw.printlnIdent("{");
				pw.add();
				pw.printlnIdent("char __s[512];");
				pw.printlnIdent("gets(__s);");
				v.genC(pw, true);
				pw.printIdent(" = malloc(strlen(__s) + 1);");
				pw.printlnIdent("strcpy(");
				v.genC(pw, true);
				pw.println(", __s);");
				pw.sub();
				pw.printlnIdent("}");
			}
		
			pw.println("");
		}
	}
	
}
