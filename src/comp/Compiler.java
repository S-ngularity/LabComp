/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
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
			kraClassList.add(classDec());
			while ( lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL )
				kraClassList.add(classDec());
			if ( lexer.token != Symbol.EOF ) {
				signalError.show("End of file expected");
			}
				
			if(symbolTable.getInGlobal("Program") == null)
				signalError.show("Source code without a class 'Program'.");
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			//System.out.println(input2);
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

	private KraClass classDec() {
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
			currentMethodReturnType = t; // tipo de retorno esperado
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
		
		return newClass;
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
				signalError.show("Static method '"+name+"' has already been declared.");
			}
			
			if(currentClass.searchStaticInstVar(name) != null)
				signalError.show("'"+name+"' has already been declared as a static variable.");
		}
		
		else
		{
			if(currentClass.searchPublicMethod(name) != null || 
			   currentClass.searchPrivateMethod(name) != null)
			{
				signalError.show("Method '"+name+"' has already been declared.");
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
		Method m = currentClass.searchSuperclassMethod(name, null);
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
		
		StatementList s = statementList();
		currentMethod.addStatement(s);
		
		// se não é void precisa de return
		if(currentMethod.getType() != Type.voidType){
			if(!hasReturn){
				signalError.show("Missing 'return' statement in method '"+currentMethod.getName()+"'.");
			}
			else{
				// ----> VERIFICAÇÃO DO TIPO DE RETORNO AQUI ou embaixo
			}
		}
		else{ // se for void não pode ter return
			if(hasReturn){
				signalError.show("Illegal 'return' statement. Method returns 'void'.", true);
			}
		}
		hasReturn = false;
			
		
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");
		lexer.nextToken();
		
		// variáveis locais saem do escopo
		symbolTable.removeLocalIdent();
		
		//if(!currentMethod.hasReturn())
		//	signalError.show("Missing 'return' in method '"+currentMethod.getName()+"'.");

		currentMethod = null;
	}

	private Statement localDec() {
		// LocalDec ::= Type IdList ";"
		
		StatementList stmtList = new StatementList();

		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		Variable v = new Variable(lexer.getStringValue(), type);
		
		// checa se já não foi declarado e adiciona nas vars locais
		if(symbolTable.getInLocal(v.getName()) != null){
			signalError.show("Local variable '"+v.getName()+"' has already been declared.");
		}
		else{
			symbolTable.putInLocal(v.getName(), v);
			stmtList.addElement(new LocalDecStmt(v));
		}
		
		lexer.nextToken(); // pula IDENT

		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();

			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			Variable var = new Variable(lexer.getStringValue(), type);

			// checa se já não foi declarado e adiciona nas vars locais
			if(symbolTable.getInLocal(var.getName()) != null){
				signalError.show("Local variable '"+var.getName()+"' has already been declared.");
			}
			else{
				symbolTable.putInLocal(var.getName(), var);
				stmtList.addElement(new LocalDecStmt(var));
			}

			lexer.nextToken(); // pula IDENT
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken(); // pula ;
		
		return stmtList;
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
		
		Variable v = new Variable(lexer.getStringValue(), t);
		
		if(symbolTable.getInLocal(v.getName()) != null){
			signalError.show("Local variable (parameter) '"+v.getName()+"' has already been declared.");
		}
		else{
			symbolTable.putInLocal(v.getName(), v);
			currentMethod.addParameter(v);
		}
		
		lexer.nextToken();
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
			// retorna classe como o tipo, se tiver sido declarada
			if(!isType(lexer.getStringValue())){
				signalError.show("Class '"+lexer.getStringValue()+"' has not been declared");
			}

			result = symbolTable.getInGlobal(lexer.getStringValue());
			break;
		default:
			signalError.show("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

// ----> ONDE GUARDAR AS COISAS ABAIXO?
	
		// CompStatement ::= "{" { Statement } "}"
	private Statement compositeStatement() {

		lexer.nextToken(); // pula {
		Statement s = statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("} expected");
		else
			lexer.nextToken();
		
		return s;
	}

	private StatementList statementList() {
		StatementList s = new StatementList();
		// StatementList ::= { Statement }
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET && tk != Symbol.ELSE)
			s.addElement(statement());
		
		return s;
	}

	private Statement statement() {
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */
		
		Statement s = null;

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			s = assignExprLocalDec();
			break;
		case RETURN:
			s = returnStatement();
			break;
		case READ:
			s = readStatement();
			break;
		case WRITE:
			s = writeStatement();
			break;
		case WRITELN:
			s = writelnStatement();
			break;
		case IF:
			s = ifStatement();
			break;
		case BREAK:
			s = breakStatement();
			break;
		case WHILE:
			s = whileStatement();
			break;
		case SEMICOLON:
			s = nullStatement();
			break;
		case LEFTCURBRACKET:
			s = compositeStatement();
			break;
		default:
			signalError.show("Statement expected");
		}
		return s;
	}

	/*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	// AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	private Statement assignExprLocalDec() {
		
		Statement s;

		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token � uma classe declarada textualmente antes desta
				// instru��o
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue()) && lexer.isNextTokenIdent()) ) {
			/*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 * 
			 * AssignExprLocalDec ::= Expression [ "=" Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ";"
			 */
			
			s = localDec();
			return s;
		}
		
		// é uma declaração de objeto de uma classe que não foi declarada
		else if(lexer.token == Symbol.IDENT && !isType(lexer.getStringValue()) && lexer.isNextTokenIdent())
		{
			signalError.show("Type '"+lexer.getStringValue()+"' was not found.");
		}

		/*
		 * AssignExprLocalDec ::= Expression [ "=" Expression ]
		 */
		
		Expr left, right=null;
		left = expr();
		
		if ( lexer.token == Symbol.ASSIGN ) {
			lexer.nextToken();
			right = expr();
			
			if(right.getType() == Type.voidType){
				signalError.show("Expression expected in the right-hand side of assignment");
			}
			
			// ----> VERIFICAÇÃO SEMÂNTICA DE TIPOS AQUI
			if( !typeCheck(left.getType(), right.getType()) ){
				signalError.show("Right type '"+right.getType().getName()+"' cannot be assigned to left type '"+left.getType().getName()+"'.");
			}
			
			if ( lexer.token != Symbol.SEMICOLON )
				signalError.show("';' expected", true);
			else
				lexer.nextToken();
			
			return new AssignStmt(left, right);
		}

		if(left instanceof MessageSend)
		{
			lexer.nextToken(); // pula ; após chamada de método isolada
			
			MessageSend msgSend = (MessageSend) left;
			return new MessageSendStatement(msgSend);
		}
		
		throw new NullPointerException();
	}
	
	// IfStat ::= “if” “(” Expression “)” Statement [ “else” Statement ]
	private Statement ifStatement() {
		
		Expr e;
		Statement ifStmt, elseStmt;

		lexer.nextToken(); // pula if
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		e = expr();
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();

		ifStmt = statement();
		
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken(); // pula else
			elseStmt = statement();
		}
		else{
			elseStmt = null;
		}
		
		return new IfStmt(e, ifStmt, elseStmt);
	}

	// WhileStat ::= “while” “(” Expression “)” Statement
	private Statement whileStatement() {
		
		insideWhile++;
		
		Expr e;
		Statement s;

		lexer.nextToken(); // pula while
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		e = expr();
		
		if(e.getType() != Type.booleanType){
			signalError.show("Non-boolean expression in  'while' command.");
		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		s = statement();
		
		insideWhile--;
		
		return new WhileStmt(e, s);
	}

	// ReturnStat ::= "return" Expression ";"
	private Statement returnStatement() {
		
		Expr e;

		lexer.nextToken(); // pula return
		
		e = expr();
		
		// ----> necessário checar se o tipo de retorno esperado é correto
		if(e.getType() != currentMethodReturnType){
			signalError.show("Type error: type of the expression returned is not the declared return type");
		}
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		hasReturn = true;
		return new ReturnStmt(e);
	}

	// ReadStat ::= “read” “(” LeftValue { “,” LeftValue } “)”
	private Statement readStatement() {
		
		ExprList valuesToRead = new ExprList();
		
		lexer.nextToken(); // pula read
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		while (true)
		{
			//if ( lexer.token != Symbol.IDENT )
			//	signalError.show("Command 'read' is incomplete.");

			Expr e = expr();
			
			if(e instanceof IdExpr || e instanceof StaticInstVarAccessExpr || e instanceof SelfInstVarAccessExpr)
			{
				if(e.getType() != Type.intType &&
					e.getType() != Type.stringType)
				{
					signalError.show("Command 'read' does not accept variables of type '"+e.getType().getName()+"', only 'int' and 'String'.");
					break;
				}

				valuesToRead.addElement(e);

				if ( lexer.token == Symbol.COMMA )
					lexer.nextToken();
				else
					break;
			}
			
			else
			{
				signalError.show("Command 'read' only accepts variables.");
				break;
			}
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new ReadStmt(valuesToRead);
	}

	// WriteStat ::= “write” “(” ExpressionList “)”
	private Statement writeStatement() {

		lexer.nextToken(); // pula write
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		ExprList exprlist = exprList();
		Iterator<Expr> exprs = exprlist.elements();
		while(exprs.hasNext()){
			Expr e = exprs.next();
			if(e.getType() == Type.booleanType){
				signalError.show("Command 'write' does not accept 'boolean' expressions");
			}
			else if(	e.getType() != Type.booleanType &&
					e.getType() != Type.intType &&
					e.getType() != Type.stringType && 
					e.getType() != Type.voidType){
				
				signalError.show("Command 'write' does not accept objects");
			}
		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new WriteStmt(exprlist);
	}

	private Statement writelnStatement() {

		lexer.nextToken(); // pula writeln
		
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		
		ExprList exprlist = exprList();
		Iterator<Expr> exprs = exprlist.elements();
		while(exprs.hasNext()){
			Expr e = exprs.next();
			if(e.getType() == Type.booleanType){
				signalError.show("Command 'writeln' does not accept 'boolean' expressions");
			}
			else if(	e.getType() != Type.booleanType &&
					e.getType() != Type.intType &&
					e.getType() != Type.stringType && 
					e.getType() != Type.voidType){
				
				signalError.show("Command 'writeln' does not accept objects");
			}
		}
		
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new WritelnStmt(exprlist);
	}

	private Statement breakStatement() {
		if(insideWhile == 0){
			signalError.show("'break' statement found outside a 'while' statement");
		}
		lexer.nextToken(); // pula break
		
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		
		return new BreakStatement();
		
	}

	private Statement nullStatement() {
		lexer.nextToken(); // pula ;
		
		return new NullStatement();
	}
	
	private ExprList realParameters() {
		ExprList anExprList = new ExprList();

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
			// se forem de tipos compatíveis
			if(typeCheck(left.getType(), right.getType())){
			/*	if(op == Symbol.EQ || op == Symbol.NEQ){
					if(left.getType() == Type.nullType){ // precisa saber quando é nullType
						signalError.show("Incompatible types cannot be compared with '"+
							op+"' because the result will always be 'false'.");
					}
				}
			*/
				if(op == Symbol.LE || op == Symbol.LT ||
					op == Symbol.GE || op == Symbol.GT){
					// apenas int pode ter essas operações
					if(left.getType() != Type.intType)
						signalError.show("type '"+left.getType().getName()+
							  "' does not support operator '"+op+"'.");
				}
				/*if(op == Symbol.EQ || op == Symbol.NEQ){ // EQ e NEQ
					// boolean, int, string ou classes podem ter essas operações
					if(left.getType() != Type.booleanType && left.getType() != Type.intType &&
						symbolTable.getInGlobal(left.getType().getName()) == null &&
						left.getType() != Type.stringType){
						
						signalError.show("type '"+left.getType().getName()+
							  "' does not support operator '"+op+"'.");
					}
				}*/
			}
			
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
			
			if(typeCheck(left.getType(), right.getType())){
				// + e - com int
				if( (op == Symbol.PLUS || op == Symbol.MINUS) 
					  && left.getType() != Type.intType){
					
					signalError.show("type "+left.getType().getName()+
							  " does not support operation '"+op+"'.");
				}
				// || com boolean
				if(op == Symbol.OR && left.getType() != Type.booleanType){
					signalError.show("type "+left.getType().getName()+
							  " does not support operation '"+op+"'.");
				}
			}
			else{
				signalError.show("operator '"+op.toString()+"' of 'int' expects an 'int' value");
			}
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
			
			if(typeCheck(left.getType(), right.getType())){
				if( (op == Symbol.DIV || op == Symbol.MULT) && 
					  left.getType() != Type.intType){
					
					signalError.show("type '"+left.getType().getName()+
							  "' does not support operator '"+op+"'.");
				}
				// && com boolean
				if(op == Symbol.AND && left.getType() != Type.booleanType){
					signalError.show("type '"+left.getType().getName()+
							  "' does not support operator '"+op+"'.");
				}
			}
			
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
			Expr e = factor();
			if(e.getType() != Type.intType){
				signalError.show("type '"+e.getType().getName()+
							  "' does not support operator '"+op+"'.");
			}
			return new SignalExpr(op, e);
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
		String messageName, secondId;

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
			lexer.nextToken(); // pula !
			
			e = expr();
			
			if(e.getType() != Type.booleanType)
				signalError.show("Operator '!' does not accepts '"+e.getType().getName()+"' values.");
				
			return new UnaryExpr(e, Symbol.NOT);
			
		// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken(); // pula new
			
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			String className = lexer.getStringValue();
			/*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
			 */
			
			KraClass createdClass = symbolTable.getInGlobal(className);
			if(createdClass == null)
				signalError.show("'"+className+"' is not a declared class.");

			lexer.nextToken(); // pula IDENT
			
			if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
			lexer.nextToken();
			
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			
			return new NewExpr(createdClass);
		
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
			
			if(currentClass.getSuperclass() == null)
				signalError.show("'super' used in class '"+currentClass.getName()+"' that does not have a superclass.");

			KraClass foundSuperclass = null;
			Method m = currentClass.searchSuperclassMethod(messageName, foundSuperclass);

			if(m == null)
				signalError.show("Method '"+messageName+"' was not found in the superclasses of '"+currentClass.getName()+"'.");

			lexer.nextToken(); // pula nome do método
			
			exprList = realParameters();
			
			//if(lexer.token == Symbol.SEMICOLON)
			//	lexer.nextToken(); // pula ; se for statement
			
			assertCompatibleParameters(m, exprList);
			
			return new MessageSendToSuper(foundSuperclass, m, exprList);
		
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | // 1o id = classe, 2o id = static var
          	 *                 Id "." Id "(" [ ExpressionList ] ")" | // 
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();
			
			// Id
			if ( lexer.token != Symbol.DOT )
			{
				Variable v = symbolTable.getInLocal(firstId);
				
				if(v == null)
				{
					if(lexer.token == Symbol.LEFTPAR)
						signalError.show("Method calls must be associated with an object or class.");
					else
						signalError.show("Local variable '"+firstId+"' has not been declared.");
				}
				
				return new IdExpr(v);
			}
			
			// "."
			lexer.nextToken(); // coma o "."
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			secondId = lexer.getStringValue();
			lexer.nextToken();

			// Id "." Id "." Id "(" [ ExpressionList ] ")"
			if ( lexer.token == Symbol.DOT )
			{
				lexer.nextToken(); // pula segundo .
				if ( lexer.token != Symbol.IDENT )
					signalError.show("Identifier expected");
				
				messageName = lexer.getStringValue();
				lexer.nextToken();
				
				// firstId tem que ser uma classe
				KraClass firstIdClass = symbolTable.getInGlobal(firstId);
				if(firstIdClass == null)
					signalError.show("'"+firstId+"' is not a class for a static variable access.");
				
				// não está dentro da classe para acessar variáveis estáticas
				if(firstIdClass != currentClass)
					signalError.show("Static variables are private to their own classes.");
				
				// secondId tem que ser uma variável estática
				InstanceVariable accessedStaticVar = firstIdClass.searchStaticInstVar(secondId);

				if(accessedStaticVar == null)
					signalError.show("Static variable '"+secondId+"' was not found in class '"+firstId+"'.");
				
				// classe da variável estática
				KraClass secondIdClass = symbolTable.getInGlobal(accessedStaticVar.getType().getName());
				
				if(secondIdClass == null)
					signalError.show("Static variable '"+secondId+"' is not a class for a method call.");
				
				// procura método público
				Method calledMethod = secondIdClass.searchPublicMethod(messageName);

				// se tiver dentro da classe, procura nos privados
				if(calledMethod == null && currentClass == secondIdClass)
					calledMethod = secondIdClass.searchPrivateMethod(messageName);
				
				// se ainda não achou, procura nas superclasses
				if(calledMethod == null)
				{
					KraClass dummySuperclass = null;
					calledMethod = secondIdClass.searchSuperclassMethod(secondId, dummySuperclass);
				}

				if(calledMethod == null)
					signalError.show("Method '"+messageName+"' on object '"+secondId+"' was not found in its class '"+secondIdClass.getName()+"' or its superclasses.");
				
				exprList = this.realParameters();

				// checa se parâmetros passados são compatíveis
				assertCompatibleParameters(calledMethod, exprList);
				return new MessageSendToStaticVariable(firstIdClass, accessedStaticVar, calledMethod, exprList);
			}

			// Id "." Id "(" [ ExpressionList ] ")"
			else if ( lexer.token == Symbol.LEFTPAR )
			{
				exprList = this.realParameters();
				
				// se firstId é classe, secondId deve ser método estático
				// se firstId é objeto, secondId deve ser método normal
				Variable firstIdVar = symbolTable.getInLocal(firstId);
				KraClass firstIdClass = null;
				if(firstIdVar == null)
					firstIdClass = symbolTable.getInGlobal(firstId);

				Method calledStaticMethod = null;
				Method calledNormalMethod = null;

				if(firstIdClass == null && firstIdVar == null)
					signalError.show("'"+firstId+"' is neither an object nor a class.");
				else if(firstIdClass != null && firstIdVar != null)
					signalError.show("'"+firstId+"' is both an object and a class... this is not supposed to happen.");

				// firstId é uma classe
				if(firstIdClass != null)
				{
					// procura método estático público
					calledStaticMethod = firstIdClass.searchStaticPublicMethod(secondId);

					// se estiver dentro da classe, procura nos métodos privados
					if(calledStaticMethod == null && currentClass == firstIdClass)
						calledStaticMethod = firstIdClass.searchStaticPrivateMethod(secondId);

					if(calledStaticMethod == null)
						signalError.show("Static method '"+secondId+"' was not found in class '"+firstId+"'.");
				}

				// firstId é um objeto
				else if(firstIdVar != null)
				{
					// pega a classe do objeto
					KraClass classOfTheObject = symbolTable.getInGlobal(firstIdVar.getType().getName());
					
					if(classOfTheObject == null)
						signalError.show("Method call to a non-object receiver.");

					// procura método público
					calledNormalMethod = classOfTheObject.searchPublicMethod(secondId);

					// se tiver dentro da classe, procura nos privados
					if(calledNormalMethod == null && currentClass == classOfTheObject)
						calledNormalMethod = classOfTheObject.searchPrivateMethod(secondId);
					
					// se ainda não achou, procura nas superclasses
					if(calledNormalMethod == null)
					{
						KraClass dummySuperclass = null;
						calledNormalMethod = classOfTheObject.searchSuperclassMethod(secondId, dummySuperclass);
					}

					if(calledNormalMethod == null)
						signalError.show("Method '"+secondId+"' on object '"+firstId+"' was not found in its class '"+classOfTheObject.getName()+"' or its superclasses.");
				}

				// checa se parâmetros passados são compatíveis
				if(calledStaticMethod != null)
				{
					assertCompatibleParameters(calledStaticMethod, exprList);
					return new MessageSendToClass(firstIdClass, calledStaticMethod, exprList);
				}
				else if (calledNormalMethod != null)
				{
					assertCompatibleParameters(calledNormalMethod, exprList);
					return new MessageSendToVariable(firstIdVar, calledNormalMethod, exprList);
				}
			}

			// Id "." Id
			else
			{
				KraClass calledClass = symbolTable.getInGlobal(firstId);

				if(calledClass == null)
					signalError.show("'"+firstId+"' is not a class for a static variable access.");

				InstanceVariable staticInstVar = calledClass.searchStaticInstVar(secondId);

				if(staticInstVar == null)
					signalError.show("'"+secondId+"' is not a static variable of class '"+firstId+"'.");

				return new StaticInstVarAccessExpr(calledClass, staticInstVar);
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
			
			lexer.nextToken(); // pula "this"
			
			if(currentMethod.isStatic())
				signalError.show("Usage of 'this' inside a static method.");
			
			// "this"
			if ( lexer.token != Symbol.DOT )
			{
				return new ThisExpr(currentClass);
			}
			
			// "."
			lexer.nextToken(); // pula .
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			
			secondId = lexer.getStringValue();
			lexer.nextToken(); // pula Id
			
			
			// "this" "." Id "(" [ ExpressionList ] ")"
			if ( lexer.token == Symbol.LEFTPAR ) {
				/*
				 * Confira se a classe corrente possui um método cujo nome é
				 * 'ident' e que pode tomar os par�metros de ExpressionList
				 */
				exprList = this.realParameters();
				
				Method calledNormalMethod = currentClass.searchPrivateMethod(secondId);
				
				if(calledNormalMethod == null)
					calledNormalMethod = currentClass.searchPublicMethod(secondId);
					
				// se ainda não achou, procura nas superclasses
				if(calledNormalMethod == null)
				{
					KraClass dummySuperclass = null;
					calledNormalMethod = currentClass.searchSuperclassMethod(secondId, dummySuperclass);
				}

				if(calledNormalMethod == null)
					signalError.show("Method '"+secondId+"' on object 'this' was not found in its class '"+currentClass.getName()+"' or its superclasses.");

				// checa se parâmetros passados são compatíveis
				assertCompatibleParameters(calledNormalMethod, exprList);
				
				return new MessageSendToSelf(currentClass, calledNormalMethod, exprList);
			}
			
			// "this" "." Id "." Id "(" [ ExpressionList ] ")"
			else if ( lexer.token == Symbol.DOT )
			{
				lexer.nextToken(); // pula segundo .
				if ( lexer.token != Symbol.IDENT )
					signalError.show("Identifier expected");
				
				messageName = lexer.getStringValue();
				lexer.nextToken();
				
				// secondId tem que ser uma variável dessa classe
				InstanceVariable accessedVar = currentClass.searchInstVar(secondId);

				if(accessedVar == null)
					signalError.show("Instance variable '"+secondId+"' was not found in class '"+currentClass.getName()+"'.");
				
				// classe da variável de instância
				KraClass secondIdClass = symbolTable.getInGlobal(accessedVar.getType().getName());
				
				if(secondIdClass == null)
					signalError.show("Instance variable '"+secondId+"' is not a class for a method call.");
				
				// procura método público
				Method calledMethod = secondIdClass.searchPublicMethod(messageName);

				// se tiver dentro da classe, procura nos privados
				if(calledMethod == null && currentClass == secondIdClass)
					calledMethod = secondIdClass.searchPrivateMethod(messageName);
				
				// se ainda não achou, procura nas superclasses
				if(calledMethod == null)
				{
					KraClass dummySuperclass = null;
					calledMethod = secondIdClass.searchSuperclassMethod(secondId, dummySuperclass);
				}

				if(calledMethod == null)
					signalError.show("Method '"+messageName+"' on object '"+secondId+"' was not found in its class '"+secondIdClass.getName()+"' or its superclasses.");
				
				exprList = this.realParameters();
				
				// checa se parâmetros passados são compatíveis
				assertCompatibleParameters(calledMethod, exprList);

				return new MessageSendToSelfVariable(currentClass, accessedVar, calledMethod, exprList);
			}
			
			// "this" "." Id
			else
			{
				InstanceVariable instVar = currentClass.searchInstVar(secondId);

				if(instVar == null)
					signalError.show("'"+secondId+"' is not a variable of class '"+currentClass.getName()+"'.");

				return new SelfInstVarAccessExpr(currentClass, instVar);
			}
		
			default:
				signalError.show("Expression expected");
		}
		
		signalError.show("Expression expected");
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
	
	public boolean typeCheck(Type leftType, Type rightType){

		if(rightType != null){
			//tipos básicos (boolean, int e string) e iguais
			if(	leftType == Type.booleanType ||
				leftType == Type.intType ||
				leftType == Type.stringType){
				// mesmo nome
				if(leftType.getName().equals(rightType.getName())){
					return true;
				}
				else{
					return false;
				}
			}
			// direita é subclasse da esquerda (classe é subclasse dela mesma)
			KraClass rightClass = symbolTable.getInGlobal(rightType.getName());

			if(rightClass != null){
				KraClass leftClass = symbolTable.getInGlobal(leftType.getName());
				
				if(leftClass == null)
					return false;

				if(rightClass.getName().equals(leftClass.getName()) ){ // mesma classe
					return true;
				}

				KraClass superRight = rightClass.getSuperclass();
				while(superRight != null){
					if(superRight.getName().equals(leftClass.getName())){
						return true;
					}
					superRight = superRight.getSuperclass();
				}
			}
		}
		// esquerda é classe e direita é null
		if(symbolTable.getInGlobal(leftType.getName()) != null &&
			  rightType == Type.nullType){
			return true;
		}
		
		return false;
	}
	
	private void assertCompatibleParameters(Method m, ExprList e)
	{
		// checa se parâmetros passados são compatíveis
		Method dummyMethod = new Method("dummy", m.getType(), false, false);
		
		Iterator<Expr> it2 = e.elements();
		while(it2.hasNext())
		{
			Type t = it2.next().getType();
			dummyMethod.addParameter(new Variable("dummy", t));
		}

		if(!m.hasSameSignature(dummyMethod))
			signalError.show("Incompatible parameters passed to method '"+m.getName()+"'.");
	}
	
	
	private int insideWhile = 0;
	private boolean hasReturn = false;
	private Type currentMethodReturnType = null;
}
