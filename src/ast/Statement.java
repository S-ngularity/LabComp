/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;

abstract public class Statement {
	
	public ArrayList<Expr> exprList = new ArrayList();
	
	private boolean grantedReturn = false, possibleBreak = false;
	
	public void checkHasGrantedReturn()
	{
		grantedReturn = true;
	}
	
	public void checkHasPossibleBreak()
	{
		possibleBreak = true;
	}
	
	public boolean hasGrantedReturn()
	{
		return grantedReturn;
	}
	
	public boolean hasPossibleBreak()
	{
		return possibleBreak;
	}

	abstract public void genKra(PW pw);
	abstract public void genC(PW pw);

}
