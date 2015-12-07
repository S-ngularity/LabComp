/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class MethodList {

    public MethodList() {
       methodList = new ArrayList<Method>();
    }

    public void addElement(Method method) {
       methodList.add( method );
    }

    public Iterator<Method> elements() {
    	return this.methodList.iterator();
    }

    public int getSize() {
        return methodList.size();
    }
	
	public Method getMethod(String methodName)
	{
		for(Method m : methodList)
		{
			if(m.getName().equals(methodName))
				return m;
		}
		
		return null;
	}
	
	public int getMethodIndex(String methodName)
	{
		int i = 0;
		
		for(Method m : methodList)
		{
			if(m.getName().equals(methodName))
				return i;
			
			i++;
		}
		
		return -1;
	}
	
	public void set(int index, Method m)
	{
		methodList.set(index, m);
	}

    private ArrayList<Method> methodList;

}
