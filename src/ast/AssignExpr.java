/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class AssignExpr extends Statement{
	
	Expr left, right;
	
	public AssignExpr(Expr l, Expr r){
		this.left = l;
		this.right = r;
	}

	@Override
	public void genKra(PW pw) {
		left.genKra(pw, true);
		
		if(right!= null){
			pw.print(" = ");
			right.genKra(pw, true);
		}
		
		pw.println(";");
	}
	
}
