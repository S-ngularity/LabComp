/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class NullStatement extends Statement{

	@Override
	public void genKra(PW pw) {
		pw.printIdent(";");
	}
	
	@Override
	public void genC(PW pw) {
		pw.printIdent(";");
	}
	
}
