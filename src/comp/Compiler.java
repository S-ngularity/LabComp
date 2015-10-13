
package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {
	
	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private SignalError	signalError;
	
	private KraClass currentClass;
	private Method currentMethod;

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new SignalError(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);
		
		currentClass = null;

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= KraClass { KraClass }
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		ArrayList<KraClass> kraClassList = new ArrayList<>();
		Program program = new Program(kraClassList, metaobjectCallList, compilationErrorList);
		try {
			while ( lexer.token == Symbol.MOCall ) {
				metaobjectCallList.add(metaobjectCall());
			}
			classDec();
			while ( lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL )
				classDec();
			if ( lexer.token != Symbol.EOF ) {
				signalError.show("End of file expected");
			}
				
			if(symbolTable.getInGlobal("Program") == null)
				signalError.show("Source code without a class 'Program'.");
		}
		catch( RuntimeException e) {
			//e.printStackTrace();
			// if there was an exception, there is a compilation signalError
		}
		
		return program;
	}

	/**  parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     *     public void run() { } <br>
     * end <br>
     * </code>
     * 
	   
	 */
	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		//MOCall ::= “@” Id [ “(” { MOParam } “)” ]
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			
			//MOParam ::= IntValue | StringValue | Id
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA ) 
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR ) 
				signalError.show("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.show("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.show("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.show("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.show("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )  
				signalError.show("The fourth parameter of metaobject 'ce' should be a literal string");
			
		}
			
		return new MetaobjectCall(name, metaobjectParamList);
	}

	private void classDec() {
		// Note que os m�todos desta classe n�o correspondem exatamente �s
		// regras
		// da gram�tica. Este m�todo classDec, por exemplo, implementa
		// a produ��o KraClass (veja abaixo) e partes de outras produ��es.

		/*
		 * KraClass ::= ``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member } 
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";" 
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}" 
		 */
		
		// checa por 'final'
		boolean isClassFinal = false;
		if(lexer.token == Symbol.FINAL)
		{
			isClassFinal = true;
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.CLASS ) signalError.show("'class' expected");

		// nome da classe
		lexer.nextToken();
		if ( lexer.token != Symbol.IDENT )
			signalError.show(SignalError.ident_expected);

		String className = lexer.getStringValue();
		
		KraClass newClass = new KraClass(className, isClassFinal);
		
		// checa se já foi declarada
		if(symbolTable.getInGlobal(className) != null)
		{
			signalError.show("Class '"+className+"' has already been declared.");
		}
		
		else
		{
			symbolTable.putInGlobal(className, newClass);
		}

		currentClass = newClass;
		
		// extends Id
		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Class expected for inheritance.");
			String superclassName = lexer.getStringValue();
			
			if(superclassName.equals(className))
				signalError.show("Class cannot inherit from itself.");
			
			// checa se classe existe para ser herdada (e não é final) e herda
			KraClass superclass = symbolTable.getInGlobal(superclassName);
			if(superclass == null)
				signalError.show("Class '"+superclassName+"' is undeclared. It cannot be inherited from.");
			
			else if(superclass.isFinal())
				signalError.show("Class '"+superclassName+"' is final. It cannot be inherited from.");
			
			else
				newClass.setSuperclass(superclass);
				

			lexer.nextToken();
		}

		// MemberList ::= { Qualifier Member }
		// {
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.show("{ expected", true);
		lexer.nextToken();

		// Qualifier ::= [ “final” ] [ “static” ] ( “private” | “public”)
		while (lexer.token == Symbol.FINAL || lexer.token == Symbol.STATIC || 
			   lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC)
		{
			// ["final"]
			boolean isMemberFinal = false;
			if(lexer.token == Symbol.FINAL)
			{
				isMemberFinal = true;
				lexer.nextToken();
			}
			
			// ["static"]
			boolean isMemberStatic = false;
			if(lexer.token == Symbol.STATIC)
			{
				isMemberStatic = true;
				lexer.nextToken();
				
				if(lexer.token == Symbol.FINAL)
				{
					isMemberFinal = true;
					lexer.nextToken();
				}
			}

			// ("private" | "public")
			Symbol qualifier;
			switch (lexer.token) {
			case PRIVATE:
				lexer.nextToken();
				qualifier = Symbol.PRIVATE;
				break;
			case PUBLIC:
				lexer.nextToken();
				qualifier = Symbol.PUBLIC;
				break;
			default:
				signalError.show("'private' or 'public' expected.");
				qualifier = Symbol.PUBLIC;
			}

			// Member ::= InstVarDec | MethodDec
			// MethodDec ::= Type Id “(” [ FormalParamDec ] “)” “{” StatementList “}”
			// InstVarDec ::= Type IdList “;”
			Type t = type();
			
			// nome
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			String name = lexer.getStringValue();
			
			// se (, é método
			lexer.nextToken();
			if ( lexer.token == Symbol.LEFTPAR )
				methodDec(t, name, qualifier, isMemberFinal, isMemberStatic);
			
			else if(isMemberFinal)
				signalError.show("Cannot declare 'final' variable.");
			
			else if ( qualifier != Symbol.PRIVATE )
				signalError.show("Instance variables can only be 'private'.");
			
			else 
				instanceVarDec(t, name, isMemberStatic);
		}
		
		if(currentClass.getName().equals("Program") && currentClass.searchPublicMethod("run") == null)
			signalError.show("Method 'run' was not found in class 'Program'.");
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("'public'/'private' or '}' expected");
		lexer.nextToken();
		
		currentClass = null;
	}

	private void instanceVarDec(Type type, String name, boolean isVarStatic) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		
		// checa se já foi declarada e adiciona a classe
		addInstVar(type, name, isVarStatic);

		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			String variableName = lexer.getStringValue();
			
			// checa se já foi declarada e adiciona a classe
			addInstVar(type, variableName, isVarStatic);
			
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}
	
	private void addInstVar(Type type, String name, boolean isVarStatic)
	{
		if(isVarStatic)
		{
			if(currentClass.searchStaticInstVar(name) != null)
				signalError.show("Static instance variable '"+name+"' has already been declared.");
			currentClass.addStaticInstanceVar(new InstanceVariable(name, type));
		}
		
		else
		{
			if(currentClass.searchInstVar(name) != null)
				signalError.show("Instance variable '"+name+"' has already been declared.");
			currentClass.addInstanceVar(new InstanceVariable(name, type));
		}
	}

	private void methodDec(Type type, String name, Symbol qualifier, 
							boolean isMethodFinal, boolean isMethodStatic) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
		
		// se é estático, checa se não há outro método estático com o mesmo nome
		if(isMethodStatic)
		{
			if(currentClass.searchStaticPublicMethod(name) != null || 
				currentClass.searchStaticPrivateMethod(name) != null)
			{
				signalError.show("Static method '"+name+"' has already been declared;");
			}
			
			if(currentClass.searchStaticInstVar(name) != null)
				signalError.show("'"+name+"' has already been declared as a static variable.");
		}
		
		else
		{
			if(currentClass.searchPublicMethod(name) != null || 
			   currentClass.searchPrivateMethod(name) != null)
			{
				signalError.show("Method '"+name+"' has already been declared;");
			}
			
			if(currentClass.searchInstVar(name) != null)
				signalError.show("'"+name+"' has already been declared as an instance variable.");
		}
		
		// final dentro de classe final
		if(currentClass.isFinal() && isMethodFinal)
			signalError.show("Cannot declare final method inside a final class "
							+ "(all methods are already final).");
		
		// final e estático (não faz sentido de acordo com especificação)
		if(isMethodFinal && isMethodStatic)
			signalError.show("Cannot declare a method to be both 'final' and 'static'. Static methods can never be overriden.");
		
		
		Method newMethod = new Method(name, type, isMethodFinal, isMethodStatic);
		currentMethod = newMethod;
		
		// adiciona lista de parâmetros ao método
		lexer.nextToken(); // pula (
		if ( lexer.token != Symbol.RIGHTPAR ) formalParamDec();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		// checa se redefine método final (não-estático)
		Method m = currentClass.searchSuperclassMethod(name);
		if(m != null && !currentMethod.isStatic() && currentMethod.hasSameSignature(m) && m.isFinal())
			signalError.show("Method '"+name+"' is overriding a final method.");
		
		// checa se tem mesma assinatura de método redefinido
		if(m != null && !currentMethod.isStatic() && !currentMethod.hasSameSignature(m))
			signalError.show("Method '"+name+"' has a different signature from overriden method in "
							 + "superclass '"+currentClass.getSuperclass().getName()+"'.");
		
		// adiciona método à classe
		if(isMethodStatic)
		{
			if(qualifier == Symbol.PRIVATE)
				currentClass.addStaticPrivateMethod(newMethod);
			else
				currentClass.addStaticPublicMethod(newMethod);
		}
		
		else
		{
			if(qualifier == Symbol.PRIVATE)
				currentClass.addPrivateMethod(newMethod);
			else
				currentClass.addPublicMethod(newMethod);
		}
		
		// checa semântica do método Program.run()
		if(currentClass.getName().equals("Program") && currentMethod.getName().equals("run"))
		{
			if(!currentMethod.hasSameSignature(new Method("run", Type.voidType, false, false)))
				signalError.show("Program.run() must return type 'void' and cannot take parameters.");
			if(qualifier != Symbol.PUBLIC)
				signalError.show("Program.run() must be public.");
			if(currentMethod.isStatic())
				signalError.show("Program.run() cannot be static.");
		}

		// corpo do método
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.show("{ expected");
		lexer.nextToken();
		
		statementList();
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");
		lexer.nextToken();
		
		//if(!currentMethod.hasReturn())
		//	signalError.show("Missing 'return' in method '"+currentMethod.getName()+"'");

		currentMethod = null;
	}

	private void localDec() {
		// LocalDec ::= Type IdList ";"

		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		Variable v = new Variable(lexer.getStringValue(), type);
		
		// ----> CHECAR SE JÁ FOI DECLARADO E ADICIONAR AO ESCOPO LOCAL
		if(symbolTable.get(v.getName()) != null){
			signalError.show("Variable '"+v.getName()+"' has already declared");
		}
		else{
			symbolTable.putInLocal(v.getName(), v);
		}
		
		lexer.nextToken(); // pula IDENT

		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			v = new Variable(lexer.getStringValue(), type);

			// ----> CHECAR SE JÁ FOI DECLARADO E ADICIONAR AO ESCOPO LOCAL
			if(symbolTable.get(v.getName()) != null){
				signalError.show("Variable '"+v.getName()+"' has already declared");
			}
			else{
				symbolTable.putInLocal(v.getName(), v);
			}

			lexer.nextToken(); // PULA IDENT
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken(); // pula ;
		
		// ID LIST? fica no while
	}

	private void formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }

		paramDec();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			paramDec();
		}
	}

	private void paramDec() {
		// ParamDec ::= Type Id

		Type t = type();
		
		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		String paramName = lexer.getStringValue();
		lexer.nextToken();
		
		currentMethod.addParameter(new Variable(paramName, t));
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result;

		switch (lexer.token) {
		case VOID:
			result = Type.voidType;
			break;
		case INT:
			result = Type.intType;
			break;
		case BOOLEAN:
			result = Type.booleanType;
			break;
		case STRING:
			result = Type.stringType;
			break;
		case IDENT:
			// # corrija: fa�a uma busca na TS para buscar a classe
			// IDENT deve ser uma classe.
			
			// ----> BUSCAR NA GLOBAL TABLE PELO NOME DA CLASSE
			if(!isType(lexer.getStringValue())){
				signalError.show("Class '"+lexer.getStringValue()+"' has not been declared");
			}

			result = null;
			break;
		default:
			signalError.show("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

// ----> ONDE GUARDAR AS COISAS ABAIXO?
	
	private void compositeStatement() {

		lexer.nextToken(); // pula {
		statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("} expected");
		else
			lexer.nextToken();
	}

	private void statementList() {
		// CompStatement ::= "{" { Statement } "}"
		// StatementList ::= { Statement }
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET && tk != Symbol.ELSE)
			statement();
	}

	private void statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			assignExprLocalDec();
			break;
		case RETURN:
			returnStatement();
			break;
		case READ:
			readStatement();
			break;
		case WRITE:
			writeStatement();
			break;
		case WRITELN:
			writelnStatement();
			break;
		case IF:
			ifStatement();
			break;
		case BREAK:
			breakStatement();
			break;
		case WHILE:
			whileStatement();
			break;
		case SEMICOLON:
			nullStatement();
			break;
		case LEFTCURBRACKET:
			compositeStatement();
			break;
		default:
			signalError.show("Statement expected");
		}
	}

	/*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	// AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	private Expr assignExprLocalDec() {

		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token � uma classe declarada textualmente antes desta
				// instru��o
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue()) && lexer.isNextTokenIdent()) ) {
			/*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
			 */
			
			localDec();
			return null; // (?)
		}

		/*
		 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
		 */
		expr();
		if ( lexer.token == Symbol.ASSIGN ) {
			lexer.nextToken();
			expr();
			if ( lexer.token != Symbol.SEMICOLON )
				signalError.show("';' expected", true);
			else
				lexer.nextToken();
		}

		// ----> TALVEZ DEVA-SE CHECAR O ; E DAR NEXTTOKEN AQUI FORA EM VEZ DE DENTRO DO IF?

		return null;
	}
	
	// IfStat ::= “if” “(” Expression “)” Statement [ “else” Statement ]
	private void ifStatement() {

		lexer.nextToken(); // pula if
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		expr();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();

		statement();
		
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken(); // pula else
			statement();
		}
	}

	// WhileStat ::= “while” “(” Expression “)” Statement
	private void whileStatement() {

		lexer.nextToken(); // pula while
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		expr();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		statement();
	}

	// ReturnStat ::= “return” Expression
	private void returnStatement() {

		lexer.nextToken(); // pula return
		
		expr();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	// ReadStat ::= “read” “(” LeftValue { “,” LeftValue } “)”
	private void readStatement() {
		
		lexer.nextToken(); // pula read
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		while (true)
		{
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken(); // pula 'this'
				
				if ( lexer.token != Symbol.DOT ) signalError.show(". expected");
				lexer.nextToken();
			}

			if ( lexer.token != Symbol.IDENT )
				signalError.show("Command 'read' is incomplete.");

			String name = lexer.getStringValue();
			// ----> CHECAR SE NAME EXISTE COMO INSTVAR DE THIS OU COMO LOCAL VAR
			// ----> CHECAR ident.staticVar AQUI? EXPR()?
			lexer.nextToken();

			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	// WriteStat ::= “write” “(” ExpressionList “)”
	private void writeStatement() {

		lexer.nextToken(); // pula write
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		exprList();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void writelnStatement() {

		lexer.nextToken(); // pula writeln
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		exprList();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void breakStatement() {
		lexer.nextToken(); // pula break
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
	}

	private void nullStatement() {
		lexer.nextToken(); // pula ;
	}
	
	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		return anExprList;
	}

	// ExpressionList ::= Expression { "," Expression }
	private ExprList exprList() {

		ExprList anExprList = new ExprList();
		
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		
		return anExprList;
	}

	// ----> SEMANTICA DE TIPOS VÁLIDOS
	// Expression ::= SimpleExpression [ Relation SimpleExpression ]
	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	// ----> SEMANTICA DE TIPOS VÁLIDOS
	// SimpleExpression ::= Term { LowOperator Term }
	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	// ----> SEMANTICA DE TIPOS VÁLIDOS
	// Term ::= SignalFactor { HighOperator SignalFactor }
	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	// ----> SEMANTICA DE TIPOS VÁLIDOS
	// SignalFactor ::= [ Signal ] Factor
	private Expr signalFactor() {
		Symbol op;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else
			return factor();
	}

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 * 
	 * BasicValue ::= IntValue | BooleanValue | StringValue 
	 * BooleanValue ::=  "true" | "false" 
	 * ObjectCreation ::= "new" Id "(" ")" 
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 Id  |
	 *                 Id "." Id | 
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" | 
	 *                 "this" "." Id | 
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {

		Expr e;
		ExprList exprList;
		String messageName, ident;

		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
		
		// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;

		// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			
		// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);

		// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			e = expr();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			return new ParenthesisExpr(e);

		// "null"
		case NULL:
			lexer.nextToken();
			return new NullExpr();
			
		// "!" Factor
		case NOT:
			lexer.nextToken();
			e = expr();
			return new UnaryExpr(e, Symbol.NOT);
			
		// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			String className = lexer.getStringValue();
			/*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
			 */

			lexer.nextToken();
			
			if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
			lexer.nextToken();
			
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			/*
			 * return an object representing the creation of an object
			 */
			return null;
		
		/*
		 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
		 *                 Id  |
		 *                 Id "." Id | 
		 *                 Id "." Id "(" [ ExpressionList ] ")" |
		 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
		 *                 "this" | 
		 *                 "this" "." Id | 
		 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
		 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
		 */
		case SUPER:
			// "super" "." Id "(" [ ExpressionList ] ")"
			lexer.nextToken(); // pula super
			
			if ( lexer.token != Symbol.DOT ) {
				signalError.show("'.' expected");
			}
			else
				lexer.nextToken();
			
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			messageName = lexer.getStringValue();
			/*
			 * para fazer as confer�ncias sem�nticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			
			exprList = realParameters();
			
			break;
		
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// Id
				// retorne um objeto da ASA que representa um identificador
				return null;
			}
			else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.show("Identifier expected");
				}
				else {
					// Id "." Id
					lexer.nextToken();
					ident = lexer.getStringValue();
					if ( lexer.token == Symbol.DOT ) {
						// Id "." Id "." Id "(" [ ExpressionList ] ")"
						/*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
						 */
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.show("Identifier expected");
						messageName = lexer.getStringValue();
						lexer.nextToken();
						exprList = this.realParameters();

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						exprList = this.realParameters();
						/*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
						 */
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
					}
				}
			}
			break;
		case THIS:
			/*
			 * Este 'case THIS:' trata os seguintes casos: 
          	 * PrimaryExpr ::= 
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
				return null;
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.show("Identifier expected");
				ident = lexer.getStringValue();
				lexer.nextToken();
				// j� analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
					// "this" "." Id "(" [ ExpressionList ] ")"
					/*
					 * Confira se a classe corrente possui um m�todo cujo nome �
					 * 'ident' e que pode tomar os par�metros de ExpressionList
					 */
					exprList = this.realParameters();
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.show("Identifier expected");
					lexer.nextToken();
					exprList = this.realParameters();
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
					return null;
				}
			}
			break;
		default:
			signalError.show("Expression expected");
		}
		return null;
	}

	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}
}
