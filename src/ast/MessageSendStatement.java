/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendStatement extends Statement { 
	
	public MessageSendStatement(Method message, ExprList args)
	{
		messageSend = new MessageSendToSelf(null, message, args); // (?)
	}


   public void genKra( PW pw ) {
      pw.printIdent("");
      // messageSend.genKra(pw);
      pw.println(";");
   }

   private MessageSend  messageSend;

}


