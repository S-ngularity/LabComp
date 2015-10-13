/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class BreakStatement extends Statement{

	@Override
	public void genKra(PW pw) {
		pw.println("break;");
	}
	
}
