/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class NullExpr extends Expr {
    
   public void genKra( PW pw, boolean putParenthesis ) {
      pw.print("null");
   }
   
   public Type getType() {
      //# corrija
      //return null;
	   return Type.nullType;
   }
}