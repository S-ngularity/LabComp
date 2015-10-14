/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class LiteralString extends Expr {
    
    public LiteralString( String literalString ) { 
        this.literalString = literalString;
    }
    
    public void genKra( PW pw, boolean putParenthesis )
	{
        pw.print("\""+literalString+"\"");
    }
    
    public Type getType() {
        return Type.stringType;
    }
    
    private String literalString;
}
