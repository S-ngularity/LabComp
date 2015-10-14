/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;

public class OrderedClassMemberList {

    public OrderedClassMemberList() {
       orderedMemberList = new ArrayList<Object>();
    }

    public void addElement(Object m) {
       orderedMemberList.add(m);
    }

    public Iterator<Object> elements() {
        return orderedMemberList.iterator();
    }

    public int getSize() {
        return orderedMemberList.size();
    }

    private ArrayList<Object> orderedMemberList;

}
