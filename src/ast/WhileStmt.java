/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
		pw.print("while(");
		exprList.get(0);
		pw.println("){");
		pw.add();
		
		stmt.genKra(pw);
		pw.sub();
		pw.println("}");
	}
	
}
