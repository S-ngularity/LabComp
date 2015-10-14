/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Variable>();
    }
	
	public void genKra(PW pw)
	{
		Iterator<Variable> it = elements();
		
		while(it.hasNext())
		{
			Variable v = it.next();
			
			pw.print(v.getType().getName()+" "+v.getName());
			
			if(it.hasNext())
				pw.print(", ");
		}
	}

    public void addElement(Variable v) {
       paramList.add(v);
    }

    public Iterator<Variable> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }

    private ArrayList<Variable> paramList;

}
