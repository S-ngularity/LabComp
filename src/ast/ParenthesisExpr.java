/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genKra( PW pw, boolean putParenthesis ) {
        pw.print("(");
        expr.genKra(pw, false);
        pw.print(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
    
    private Expr expr;
}