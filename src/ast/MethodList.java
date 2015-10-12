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

    private ArrayList<Method> methodList;

}
