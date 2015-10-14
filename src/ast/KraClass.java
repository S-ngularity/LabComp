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
   
   public String getCname() {
      return getName();
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
  
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
