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

    private ArrayList<Statement> stList;

	@Override
	public void genKra(PW pw)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
