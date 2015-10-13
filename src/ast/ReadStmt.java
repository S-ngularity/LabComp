/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
