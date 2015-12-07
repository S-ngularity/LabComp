/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type, boolean isStatic ) {
        super(name, type);
		this.isStatic = isStatic;
    }
	
	public boolean isStatic()
	{
		return isStatic;
	}

	public void genKra(PW pw)
	{
		pw.println("private "+super.getType().getName()+" "+super.getName()+";");
	}
	
	public void genC(PW pw)
	{
		//pw.println(super.getType().getCname()+" _"+ /* nome da classe */ +"+super.getName()+";");
	}
	
	boolean isStatic;
}