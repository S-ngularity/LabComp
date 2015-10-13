/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }
	
	public InstanceVariable getInstanceVar(String instVarName)
	{
		for(InstanceVariable v : instanceVariableList)
		{
			if(v.getName().equals(instVarName))
				return v;
		}
		
		return null;
	}

    private ArrayList<InstanceVariable> instanceVariableList;

}
