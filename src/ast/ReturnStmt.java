/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class ReturnStmt extends Statement{
	
	public ReturnStmt(Expr e, Type expectedType){
		this.exprList.add(e);
		this.expectedType = expectedType;
		
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
		
		if(exprList.get(0).getType() != expectedType)
			pw.print("(" + expectedType.getCname() + ") ");
		
		exprList.get(0).genC(pw, false);
		pw.print(";");
	}

	private Type expectedType;
}
