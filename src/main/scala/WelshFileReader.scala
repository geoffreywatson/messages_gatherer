import java.io.{BufferedWriter, File, FileWriter}

import scala.collection.mutable.ListBuffer
import scala.io.Source

object WelshFileReader {

  val encoding = "ISO-8859-1"

  def welshFileLoc(fileName: String) = s"/Users/geoffwatson/Downloads/messages_gatherer/$fileName"

  def makeWelshList(fileName: String) = {
    val welsh = welshFileLoc(fileName)
    val bufferedSource = Source.fromFile(new File(welsh), "utf-16")
    val lines = bufferedSource.getLines()
    val lb = new ListBuffer[Message]()
    for(line <- lines) {
      val lineArray: Array[String] = line.trim.split('*')
      val lineOpt: Option[Message] = lineArray.length match {
        case 0 | 1 =>  None
        case 2 => {
          Some(Message(lineArray(0).trim, lineArray(1).trim, None))
        }
        case 3 => Some(Message(lineArray(0).trim, lineArray(1).trim, Some(lineArray(2).trim)))
        case _ => throw new RuntimeException(s"got unexpected line length for line $line")
      }
      lineOpt.map(lb.addOne)
    }
    val result = lb.toList
    bufferedSource.close()

    result.filter(m => !m.key.startsWith("#") && m.key.nonEmpty && m.WelshMsgTxt.isDefined)
    }

  def toWelshMap(messageList: List[Message]) = {
    messageList.collect{
      case Message(k,e,Some(w)) => (e,(w,k))
    }.toMap
  }

  def readMasterWriteWelsh(fileName: String, welshMap: Map[String, (String, String)]): List[ProcessedMessage] = {
    val bufferedSource = Source.fromFile(new File(fileName))
    val lines = bufferedSource.getLines()
    val lb = new ListBuffer[ProcessedMessage]()
    for (line <- lines) {
      val lineArray: Array[String] = line.trim.split('*')
      if(lineArray.length >1) {
        val messageKey = lineArray(0)
        val englishMessage = lineArray(1)
        val lineOpt: Option[(String, String)] = welshMap.get(englishMessage)
        val processedMessage = ProcessedMessage(messageKey, englishMessage, lineOpt)
        lb.addOne(processedMessage)
      }
    }
    lb.toList
  }

  def writeNewFileWithWelshTranslation(
                                        lookInFolder: String,
                                        welshMap: Map[String, (String, String)], service: Option[String] = None): Unit = {

    val filePath = s"/Users/geoffwatson/Downloads/messages_gatherer/$lookInFolder"

    val serviceMessages = service.getOrElse("agent_messages.csv")

    val masterMessagesPath = s"$filePath/$serviceMessages"
    val targetLocation = s"$filePath/processed.csv"

    val bs = Source.fromFile(new File(masterMessagesPath))
    val lines = bs.getLines()
    val bw = new BufferedWriter(new FileWriter(targetLocation))
    for (line <- lines) {
        val lineArray: Array[String] = line.split('*')
        if(lineArray.length > 1){
          val engMsg = lineArray(1)
          val s = stringTrim(welshMap.get(engMsg))
          bw.write(line + s)
          bw.newLine()
        } else {
          bw.write(line)
          bw.newLine()
        }
      }
    bs.close()
    bw.close()
    }

  private def stringTrim(welshOpt: Option[(String, String)]): String = {
    welshOpt match {
      case Some(v) => s"* ${v._1} * ${v._2}"
      case _ => ""
    }
  }

}
