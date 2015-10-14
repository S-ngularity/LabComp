/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class WhileStmt extends Statement{
	
	Statement stmt;
	
	public WhileStmt(Expr e, Statement s){
		this.exprList.add(e);
		this.stmt = s;
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("while(");
		exprList.get(0).genKra(pw, false);
		pw.println(")");
		pw.printlnIdent("{");
		pw.add();
		
		stmt.genKra(pw);
		
		pw.sub();
		pw.println("");
		pw.printIdent("}");
	}
	
}
