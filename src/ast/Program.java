/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.*;
import comp.CompilationError;

public class Program {

	public Program(ArrayList<KraClass> classList, ArrayList<MetaobjectCall> metaobjectCallList, 
			       ArrayList<CompilationError> compilationErrorList) {
		this.classList = classList;
		this.metaobjectCallList = metaobjectCallList;
		this.compilationErrorList = compilationErrorList;
	}


	public void genKra(PW pw) {
		for(KraClass k : classList)
		{
			k.genKra(pw);
			pw.println("");
			pw.println("");
		}
	}

	public void genC(PW pw)
	{
		pw.println("#include <stdlib.h>");
		pw.println("#include <stdio.h>");
		pw.println("#include <string.h>");
		pw.println("");
		pw.println("typedef int boolean;");
		pw.println("#define true 1");
		pw.println("#define false 0");
		pw.println("");
		pw.println("typedef");
		pw.println("void (*Func)();");
		pw.println("");
		pw.println("");
		
		
		for(KraClass k : classList)
		{
			k.genC(pw);
			pw.println("");
			pw.println("");
		}
		
		pw.printlnIdent("int main() {");
		pw.add();
		pw.printlnIdent("_class_Program *program;");
		pw.println("");
		pw.printlnIdent("program = new_Program();");
		pw.println("");
		pw.printlnIdent("( ( void (*)(_class_Program *) ) program->vt[0] )(program);"); // Nem sempre o número de run no vetor é 0. Como achar?
		pw.println("");
		pw.printlnIdent("return 0;");
		pw.sub();
		pw.printlnIdent("}");
	}
	
	public ArrayList<KraClass> getClassList() {
		return classList;
	}


	public ArrayList<MetaobjectCall> getMetaobjectCallList() {
		return metaobjectCallList;
	}
	

	public boolean hasCompilationErrors() {
		return compilationErrorList != null && compilationErrorList.size() > 0 ;
	}

	public ArrayList<CompilationError> getCompilationErrorList() {
		return compilationErrorList;
	}

	
	private ArrayList<KraClass> classList;
	private ArrayList<MetaobjectCall> metaobjectCallList;
	
	ArrayList<CompilationError> compilationErrorList;

	
}