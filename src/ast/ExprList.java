/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class ExprList {

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add(expr);
    }

    public void genKra( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genKra(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }
	
	public Iterator<Expr> elements() {
    	return this.exprList.iterator();
    }

    private ArrayList<Expr> exprList;

}
