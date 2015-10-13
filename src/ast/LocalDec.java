/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ast;

import java.util.ArrayList;

public class LocalDec extends Expr{
	
	Type type;
	ArrayList<Variable> idList;
	
	public LocalDec(){
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		for(Variable v : idList)
			pw.println(type.toString()+" "+v.getName()+";");
	}

	@Override
	public Type getType() {
		return type;
	}
	
}
