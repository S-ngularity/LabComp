/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;

abstract public class Statement {
	
	public ArrayList<Expr> exprList = new ArrayList();

	abstract public void genKra(PW pw);

}
