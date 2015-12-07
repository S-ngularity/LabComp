/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class AssignStmt extends Statement{
	
	Expr left, right;
	
	public AssignStmt(Expr l, Expr r){
		this.left = l;
		this.right = r;
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("");
		left.genKra(pw, false);
		
		if(right!= null){
			pw.print(" = ");
			right.genKra(pw, false);
		}
		
		pw.print(";");
	}
	
	@Override
	public void genC(PW pw) {
		pw.printIdent("");
		left.genC(pw, false);
		
		if(right!= null){
			pw.print(" = ");
			right.genC(pw, false);
		}
		
		pw.print(";");
	}
	
}
