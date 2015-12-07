/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class LiteralInt extends Expr {
    
    public LiteralInt( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    public void genKra( PW pw, boolean putParenthesis ) {
        pw.print("" + value);
    }
	
	public void genC( PW pw, boolean putParenthesis ) {
        pw.print("" + value);
    }
    
    public Type getType() {
        return Type.intType;
    }
    
    private int value;
}
