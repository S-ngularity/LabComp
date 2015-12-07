/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        pw.print( v.getName() );
    }
	
	public void genC( PW pw, boolean putParenthesis ) {
        pw.print( v.getCname() ); // ?? talvez precise da classe antes? mas whatever, VariableExpr nem é usado?
    }
    
    public Type getType() {
        return v.getType();
    }
    
    private Variable v;
}