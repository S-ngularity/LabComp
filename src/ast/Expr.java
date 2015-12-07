/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

abstract public class Expr {
    abstract public void genKra( PW pw, boolean putParenthesis );
	public void genC( PW pw, boolean putParenthesis ){};
      // new method: the type of the expression
    abstract public Type getType();
}