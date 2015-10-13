/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;

public class IfStmt extends Statement{
	
	public Statement ifStmt, elseStmt;
	
	public IfStmt(Expr expr, Statement ifStmt, Statement elseStmt){
		this.exprList.add(expr);
		this.ifStmt = ifStmt;
		this.elseStmt = elseStmt;
	}

	@Override
	public void genKra(PW pw) {
		pw.print("if (");
		
		for(Expr e : exprList)
			e.genKra(pw, false);
		
		pw.println("){");
		pw.add();
		
		ifStmt.genKra(pw);
		
		if(elseStmt != null){
			pw.sub();
			pw.println("}"); //fecha o if
			
			pw.println("else{");
			pw.add();
			elseStmt.genKra(pw);
		}
		
		pw.sub();
		pw.println("}");
	}
	
}
