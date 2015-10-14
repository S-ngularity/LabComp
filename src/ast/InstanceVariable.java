/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type);
    }

	public void genKra(PW pw)
	{
		pw.println("private "+super.getType().getName()+" "+super.getName()+";");
	}
}