package ast;

import java.util.*;

public class StatementList {

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

}
