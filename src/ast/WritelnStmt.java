/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

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
	
}
