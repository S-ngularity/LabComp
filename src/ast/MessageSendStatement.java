/*
	Filipe Santos Rocchi			552194
	Rafael Brandão Barbosa Fairbanks	552372
*/
package ast;

public class MessageSendStatement extends Statement { 
	
	public MessageSendStatement(MessageSend messageSend)
	{
		msgSend = messageSend;
	}

	@Override
   public void genKra( PW pw ) {
      pw.printIdent("");
      msgSend.genKra(pw, false);
      pw.print(";");
   }

   private MessageSend  msgSend;

}


