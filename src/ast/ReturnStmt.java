/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

public class ReturnStmt extends Statement{
	
	public ReturnStmt(Expr e){
		this.exprList.add(e);
	}

	@Override
	public void genKra(PW pw) {
		pw.print("return ");
		exprList.get(0).genKra(pw, true);
		pw.println(";");
	}
	
}
