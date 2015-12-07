/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

public class Method {

    public Method( String name, Type type, boolean isMethodFinal, boolean isMethodStatic ) {
        this.name = name;
        this.type = type;
		
		parameterList = new ParamList();
		
		stmtList = new StatementList();
		
		isFinal = isMethodFinal;
		isStatic = isMethodStatic;
    }
	
	public void genKra(PW pw)
	{
		pw.print(getType().getName()+" "+getName()+"(");
		parameterList.genKra(pw);
		pw.println(")");
		pw.printlnIdent("{");
		
		pw.add();
		stmtList.genKra(pw);
		pw.sub();
		
		pw.println("");
		pw.printlnIdent("}");
	}
	
	public void genC(PW pw)
	{
		if(!isStatic() && parameterList.getSize() > 0)
			pw.print(", ");
		
		parameterList.genC(pw);
		pw.println(")");
		pw.printlnIdent("{");
		
		pw.add();
		stmtList.genC(pw);
		pw.sub();
		
		pw.println("");
		pw.printlnIdent("}");
	}

    public String getName() { return name; }

    public Type getType() {
        return type;
    }
	
	public ParamList getParamList()
	{
		return parameterList;
	}
	
	public void addParameter(Variable v)
	{
		parameterList.addElement(v);
	}
	
	public void addStatement(Statement s)
	{
		stmtList.addElement(s);
	}
	
	public boolean isFinal()
	{
		return isFinal;
	}
	
	public boolean isStatic()
	{
		return isStatic;
	}
	
	public boolean hasSameSignature(Method m)
	{
		// mesmo tipo de retorno
		if(!type.getName().equals(m.type.getName()))
			return false;

		// mesmo número e tipos dos parâmetros
		if(parameterList.getSize() == m.parameterList.getSize())
		{
			Iterator<Variable> thisIt = parameterList.elements();
			Iterator<Variable> thatIt = m.parameterList.elements();
			
			while(thisIt.hasNext())
			{
				Variable thisVar = thisIt.next();
				Variable thatVar = thatIt.next();
				
				// parâmetros diferentes
				if(!thisVar.getType().getName().equals(thatVar.getType().getName()))
				{
					return false;
				}
			}
			
			return true;
		}
		
		// número diferente de parâmetros
		else
			return false;
	}

    private String name;
    private Type type;
	
	private ParamList parameterList;
	
	private StatementList stmtList;
	
	private boolean isFinal;
	private boolean isStatic;
}