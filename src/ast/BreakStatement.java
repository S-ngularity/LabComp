/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class BreakStatement extends Statement{
	
	public BreakStatement()
	{
		super.checkHasPossibleBreak();
	}

	@Override
	public void genKra(PW pw) {
		pw.printIdent("break;");
	}
	
	@Override
	public void genC(PW pw) {
		pw.printIdent("break;");
	}
	
}
