/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.ArrayList;

public class ReadStmt extends Statement{
	
	ArrayList<String> leftValues;
	
	public ReadStmt(ArrayList<String> s){
		leftValues = s;
	}

	@Override
	public void genKra(PW pw) {
		pw.print("read (");
		
		int i=0;
		for(String s : leftValues){
			if(i!=0){
				pw.print(", ");
			}
			pw.print(s);
			i++;
		}
		
		pw.println(");");
	}
	
}
