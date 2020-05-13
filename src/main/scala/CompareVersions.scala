import java.io.File

import FileReader._

import scala.collection.mutable.ListBuffer

object CompareVersions {

  def apply(compareWith: String): Unit = {
    val reportTitle = s"Compared messages currently in local branch of these services: ${services.mkString(", ")} with messages in $compareWith"
    println(s"compare with ........$compareWith")
    val dir = newDirectory()
        dir.mkdir()
        fetchMessagesFilesAndCopy(services, dir)
        val allMessages = combineMessages(dir)
        writeFinal("agents_messages.csv", allMessages, dir)
        val newMessages = readFile(dir)
        val path = s"/Users/geoffwatson/Downloads/messages_gatherer/$compareWith"
        val oldMessages = readFile(new File(path))
    val result = compare(oldMessages, newMessages)
    val finalResult = compareMessageContent(result.oldMessages, result.newMessages)
    prettyPrint(path, dir.toString, finalResult)
  }

  /*pick out all messages that do not exactly match the other set i.e. either key/message or both has changed
  * */
  def compare(oldMessages: List[Message], newMessages: List[Message]): MessageResult = {
    val oldMessagesLb = new ListBuffer[Message]()
    val newMessagesLb = new ListBuffer[Message]()
    for(om <- oldMessages){
      if(!newMessages.contains(om)) { // a message may have been deleted or the message key has been changed
        oldMessagesLb.addOne(om)
      }
    }
    for(nm <- newMessages) {
      if(!oldMessages.contains(nm)) { // a message may have been added or the message key has been changed
        newMessagesLb.addOne(nm)
      }
    }
    MessageResult(oldMessagesLb.toList, newMessagesLb.toList)
  }

  /* A key may have changed causing output from the above...this is not necessarily a problem. Now lets see if there is a
  translation for a english message.
  */
  def compareMessageContent(oldMessages: List[Message], newMessages: List[Message]): MessageResult = {
    val oldMessagesLb = new ListBuffer[Message]()
    val newMessagesLb = new ListBuffer[Message]()

    for(om <- oldMessages) {
      if(!newMessages.map(_.EnMsgTxt).contains(om.EnMsgTxt)){ //a message removed
        oldMessagesLb.addOne(om)
      }
    }
    for(nm <- newMessages) {
      if(!oldMessages.map(_.EnMsgTxt).contains(nm.EnMsgTxt)){ // a message added
        newMessagesLb.addOne(nm)
      }
    }
    MessageResult(oldMessagesLb.toList, newMessagesLb.toList)
  }

  def getFileName(path: String): String = path.takeRight(path.reverse.indexOf('/'))

  def prettyPrint(oldDirectory: String, newDirectory: String, messageResult: MessageResult): Unit = {
    println(s"***************************************")
    println(s"COMPARING MESSAGES FROM OLD DIR: ${getFileName(oldDirectory)} WITH NEW DIR: ${getFileName(newDirectory)}")
    println("")
    println("Messages that have been removed......")
    for(m <- messageResult.oldMessages) println(m.toString)
    println()
    println("Messages that have been added...........")
    for(m <- messageResult.newMessages) println(m.toString)
  }
}
