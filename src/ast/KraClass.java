/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

import java.util.Iterator;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name, boolean isClassFinal ) {
		super(name);

		isFinal = isClassFinal;
		superclass = null;

		instanceVariableList = new InstanceVariableList();
		instanceVariableStaticList = new InstanceVariableList();
		publicMethodList = new MethodList();
		privateMethodList = new MethodList();
		publicStaticMethodList = new MethodList();
		privateStaticMethodList = new MethodList();
		
		orderedMemberList = new OrderedClassMemberList();
		
		orderedCPublicMethodList = new MethodList();
   }
   
   public void genKra(PW pw)
   {
	   if(isFinal)
		   pw.print("final ");
	   
	   pw.printlnIdent("class "+super.getName());
	   pw.printlnIdent("{");
	   pw.add();
	   
	   Iterator<Object> it = orderedMemberList.elements();
	   
	   while(it.hasNext())
	   {
		   Object o = it.next();
		   
		   if(o instanceof InstanceVariable)
		   {
				InstanceVariable v = (InstanceVariable) o;

				pw.printIdent("");

				if(searchStaticInstVar(v.getName()) != null)
					pw.print("static ");

				v.genKra(pw);
		   }
		   
		   else if(o instanceof Method)
		   {
				Method m = (Method) o;
				
				pw.printIdent("");
				
				if(m.isFinal())
					pw.print("final ");

				if(m.isStatic())
				{
					pw.print("static ");
					
					if(searchStaticPrivateMethod(m.getName()) != null)
						pw.print("private ");
					else
						pw.print("public ");
				}
				
				else
				{
					if(searchPrivateMethod(m.getName()) != null)
						pw.print("private ");
					else
						pw.print("public ");
				}

				m.genKra(pw);
		   }
		   
		   if(it.hasNext())
			   pw.println("");
	   }
	   
	   pw.sub();
	   pw.printIdent("}");
   }
   
   public void genC(PW pw)
   {
	   // Adiciona métodos da superclasse na lista de métodos dessa classe.
		// Como as classes têm que ser declaradas em ordem, sempre que chegar numa subclasse,
		// a superclasse ja vai ter sido gerada e portanto sua orderedCPublicMethodList já está cheia
		// com suas respectivas superclasses (recursivamente, da mesma maneira)
		if(superclass != null)
		{
			Iterator<Method> it = superclass.orderedCPublicMethodList.elements();
			
			while(it.hasNext())
			{
				Method m = it.next();
				orderedCPublicMethodList.addElement(m);
			}
		}
		
		pw.printlnIdent("typedef");
		pw.printlnIdent("struct _St_"+ super.getName() +" {");
		pw.add();
		pw.printlnIdent("Func *vt;");
		genCMemberVars(pw);
		pw.sub();
		pw.println("");
		pw.printlnIdent("} "+ getCname() +";");
		pw.println("");
		genCStaticMemberVars(pw);
		pw.println("");
		pw.printlnIdent(getCname() + "* new_"+ super.getName() + "(void);");
		pw.println("");
		
		// métodos
		Iterator<Object> it = orderedMemberList.elements();
		while(it.hasNext())
		{
			Object o = it.next();

			if(o instanceof Method)
			{
				Method m = (Method) o;
				
				// adiciona próprios métodos na lista, colocando na mesma posição do método da
				// superclasse, se existir, ou no final, caso contrário
				if(!m.isStatic() && searchPublicMethod(m.getName()) != null)
				{
					int index = getCSuperclassMethodIndex(m.getName());
						
					if(index != -1)
						orderedCPublicMethodList.set(index, m);

					else
						orderedCPublicMethodList.addElement(m);
				}
				
				// print
				pw.print(m.getType().getCname()+" ");

				if(m.isStatic())
				{
					pw.print("_static_"+ super.getName() +"_"+m.getName()+"(");
				}
				
				else
					pw.print("_"+ super.getName() +"_"+m.getName()+"("+ getCname() +" *this");
				
				m.genC(pw);
				
				pw.println("");
			}
		}
		
		// tabela de métodos
		pw.printlnIdent("Func VTclass_"+ super.getName() +"[] = {");
		pw.add();
		genCmethodTable(pw);
		pw.sub();
		pw.printlnIdent("};");
		pw.println("");
		
		// função new_NomeDaClasse()
		pw.printlnIdent(getCname() + "* new_"+ super.getName() +"()");
		pw.printlnIdent("{");
		pw.add();
		pw.printlnIdent(getCname() + " *t;");
		pw.println("");
		pw.printlnIdent("if ( (t = malloc(sizeof("+ getCname() +"))) != NULL )");
		pw.add();
		pw.printlnIdent("t->vt = VTclass_"+ super.getName() +";");
		pw.sub();
		pw.println("");
		pw.printlnIdent("return t;");
		pw.sub();
		pw.printlnIdent("}");
		
   }
   
   
	public boolean genCmethodTable(PW pw)
	{
		// print da lista de métodos final
		Iterator<Method> itMethods = orderedCPublicMethodList.elements();
		boolean isNotFirst = false;
		
		while(itMethods.hasNext())
		{
			Method m = itMethods.next();
			
			if(isNotFirst)
			{
				pw.println(",");
				pw.printIdent("(void (*) () ) "+ m.getCname());
			}

			else
			{
				pw.printIdent("(void (*) () ) "+ m.getCname());
				isNotFirst = true;
			}
		}
		
		if(isNotFirst)
			pw.println("");
		
		return isNotFirst;
	}
	
	int getCSuperclassMethodIndex(String methodName)
	{
		if(superclass == null)
			return -1;
		
		else
			return superclass.orderedCPublicMethodList.getMethodIndex(methodName);
	}
	
	int getCMethodIndex(String methodName)
	{
		int index = orderedCPublicMethodList.getMethodIndex(methodName);

		if(index != -1)
			return index;

		else if(superclass == null)
			return -1;

		else
			return superclass.orderedCPublicMethodList.getMethodIndex(methodName);
	}
	
	public boolean genCMemberVars(PW pw)
	{
		boolean isNotFirst = false;

		if(superclass != null)
		{
			if(superclass.genCMemberVars(pw))
				isNotFirst = true;
		}

		Iterator<Object> it = orderedMemberList.elements();

		while(it.hasNext())
		{
			Object o = it.next();

			if(o instanceof InstanceVariable)
			{
				InstanceVariable v = (InstanceVariable) o;

				if(searchInstVar(v.getName()) != null)
				{
					if(isNotFirst)
					{
						pw.println(",");
						pw.printIdent(v.getType().getCname()+" _"+ getName() + "_" + v.getName());
					}

					else
					{
						pw.printIdent(v.getType().getCname()+" _"+ getName() + "_" + v.getName());
						isNotFirst = true;
					}
				}
			}
		}
		
		return isNotFirst;
	}
	
	public void genCStaticMemberVars(PW pw)
	{
		Iterator<Object> it = orderedMemberList.elements();
		
		while(it.hasNext())
		{
			Object o = it.next();

			if(o instanceof InstanceVariable)
			{
				InstanceVariable v = (InstanceVariable) o;

				if(searchStaticInstVar(v.getName()) != null)
					pw.printlnIdent(v.getType().getCname()+" _static_"+ getName() + "_" + v.getName() + ";");
			}
		}

		pw.println("");
	}
   
   public String getCname() {
		if(getName().equals("null"))
			return("NULL");
		else
			return "_class_"+ super.getName();
   }
   
   public boolean isFinal()
   {
	   return isFinal;
   }
   
   public void setSuperclass(KraClass s)
   {
	   superclass = s;
   }
   
   public KraClass getSuperclass()
   {
	   return superclass;
   }
   
   public Method searchStaticPublicMethod(String methodName)
   {
		return publicStaticMethodList.getMethod(methodName);
   }
   
   public Method searchStaticPrivateMethod(String methodName)
   {
	   return privateStaticMethodList.getMethod(methodName);
   }
   
   public Method searchPublicMethod(String methodName)
   {
	   return publicMethodList.getMethod(methodName);
   }
   
   public Method searchPrivateMethod(String methodName)
   {
	   return privateMethodList.getMethod(methodName);
   }
   
   public Method searchSuperclassMethod(String methodName, KraClass foundSuperclass)
   {
	   if(superclass == null)
	   {
		   foundSuperclass = null;
		   return null;
	   }
	   
	   else
	   {
		   return superclass.searchThisSuperclassMethod(methodName, foundSuperclass);
	   }
   }
   
   private Method searchThisSuperclassMethod(String methodName, KraClass foundSuperclass)
   {
	   Method m = searchPublicMethod(methodName);
	   
	   if(m != null)
	   {
		   foundSuperclass = this;
		   return m;
	   }
	   
	   else if(superclass == null)
	   {
		   foundSuperclass = null;
		   return null;
	   }
	   
	   else
		   return superclass.searchThisSuperclassMethod(methodName, foundSuperclass);
   }
   
   public void addStaticPublicMethod(Method m)
   {
	   publicStaticMethodList.addElement(m);
	   orderedMemberList.addElement(m);
   }
   
   public void addStaticPrivateMethod(Method m)
   {
	   privateStaticMethodList.addElement(m);
	   orderedMemberList.addElement(m);
   }
   
   public void addPublicMethod(Method m)
   {
	   publicMethodList.addElement(m);
	   orderedMemberList.addElement(m);
   }
   
   public void addPrivateMethod(Method m)
   {
	   privateMethodList.addElement(m);
	   orderedMemberList.addElement(m);
   }
   
   
   public InstanceVariable searchStaticInstVar(String name)
   {
	   return instanceVariableStaticList.getInstanceVar(name);
   }
      
   public InstanceVariable searchInstVar(String name)
   {
	   return instanceVariableList.getInstanceVar(name);
   }
   
   public void addStaticInstanceVar(InstanceVariable instVar)
   {
	   instanceVariableStaticList.addElement(instVar);
	   orderedMemberList.addElement(instVar);
   }
   
   public void addInstanceVar(InstanceVariable instVar)
   {
	   instanceVariableList.addElement(instVar);
	   orderedMemberList.addElement(instVar);
   }
   
   private String name;
   private boolean isFinal;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList, instanceVariableStaticList;
   private MethodList publicMethodList, privateMethodList, publicStaticMethodList, privateStaticMethodList;
   
   private OrderedClassMemberList orderedMemberList;
   
   protected MethodList orderedCPublicMethodList;
  
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
