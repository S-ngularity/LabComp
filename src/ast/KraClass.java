/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;
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
   
   public Method searchSuperclassMethod(String methodName)
   {
	   if(superclass == null)
		   return null;
	   
	   else
		   return superclass.searchPublicMethod(methodName);
   }
   
   public void addStaticPublicMethod(Method m)
   {
	   publicStaticMethodList.addElement(m);
   }
   
   public void addStaticPrivateMethod(Method m)
   {
	   privateStaticMethodList.addElement(m);
   }
   
   public void addPublicMethod(Method m)
   {
	   publicMethodList.addElement(m);
   }
   
   public void addPrivateMethod(Method m)
   {
	   privateMethodList.addElement(m);
   }
   
   
   public InstanceVariable searchStaticInstVar(String name)
   {
	   return instanceVariableStaticList.getInstanceVar(name);
   }
   
   public void addStaticInstanceVar(InstanceVariable instVar)
   {
	   instanceVariableStaticList.addElement(instVar);
   }
   
   public InstanceVariable searchInstVar(String name)
   {
	   return instanceVariableList.getInstanceVar(name);
   }
   
   public void addInstanceVar(InstanceVariable instVar)
   {
	   instanceVariableList.addElement(instVar);
   }
   
   private String name;
   private boolean isFinal;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList, instanceVariableStaticList;
   private MethodList publicMethodList, privateMethodList, publicStaticMethodList, privateStaticMethodList;
  
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
