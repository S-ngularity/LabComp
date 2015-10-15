/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class Variable {

    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
		
		isNull = true;
    }

    public String getName() { return name; }

    public Type getType() {
        return type;
    }
	
	public boolean isNull()
	{
		return isNull;
	}
	
	public void setIsNull(boolean isNowNull)
	{
		isNull = isNowNull;
	}

    private String name;
    private Type type;
	
	private boolean isNull;
}