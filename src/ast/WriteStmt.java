/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

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
	
}
