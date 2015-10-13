/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class ReturnStmt extends Statement{
	
	public ReturnStmt(Expr e){
		this.exprList.add(e);
	}

	@Override
	public void genKra(PW pw) {
		pw.print("return ");
		exprList.get(0).genKra(pw, false);
		pw.println(";");
	}
	
}
