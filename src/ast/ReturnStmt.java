/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class ReturnStmt extends Statement{
	
	public ReturnStmt(Expr e){
		this.exprList.add(e);
		
		super.checkHasGrantedReturn();
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("return ");
		exprList.get(0).genKra(pw, false);
		pw.print(";");
	}
	
	@Override
	public void genC(PW pw) {
		pw.printIdent("return ");
		exprList.get(0).genC(pw, true);
		pw.print(";");
	}
	
}
