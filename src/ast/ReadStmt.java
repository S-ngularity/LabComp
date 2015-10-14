/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;

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
	
}
