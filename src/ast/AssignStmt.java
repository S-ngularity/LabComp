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
			
			// cast do tipo do right pro tipo do left se right for KraClass subclasse de left
			// que classes da AST podem ser Right que precisam de cast, de acordo com a semântica de Krakatoa?
			// NewExpr e VariableExpr? algo mais?
			
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
