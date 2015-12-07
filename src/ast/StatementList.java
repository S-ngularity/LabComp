/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class StatementList extends Statement{

    public StatementList() {
       stList = new ArrayList<Statement>();
    }

    public void addElement(Statement v) {
       stList.add(v);
    }

    public Iterator<Statement> elements() {
        return stList.iterator();
    }

    public int getSize() {
        return stList.size();
    }

	@Override
	public void genKra(PW pw)
	{
		Iterator<Statement> it = elements();
		
		while(it.hasNext())
		{
			Statement s = it.next();
			
			s.genKra(pw);
			
			if(it.hasNext())
				pw.println("");
		}
	}
	
	@Override
	public void genC(PW pw)
	{
		Iterator<Statement> it = elements();
		
		while(it.hasNext())
		{
			Statement s = it.next();
			
			s.genC(pw);
			
			if(it.hasNext())
				pw.println("");
		}
	}
	
    private ArrayList<Statement> stList;
}
