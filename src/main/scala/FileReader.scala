import java.io.{BufferedWriter, File, FileWriter}
import java.time.LocalDateTime

import scala.collection.mutable.ListBuffer
import scala.io.Source

object FileReader {

  val allServices = List(
    "agent-services-account-frontend",
    "agent-subscription-frontend",
    "agent-invitations-frontend",
    "agent-usher-frontend",
    "agent-mapping-frontend",
    "agent-client-management-frontend",
    "agent-client-authorisation"
  )

  val emailTemplates = List(
    "agent_services_account_created.scala.txt",
    "client_accepted_authorisation_request.scala.txt",
    "client_expired_authorisation_request.scala.txt",
    "client_rejected_authorisation_request.scala.txt",
  )

  val services = List("agent-invitations-frontend")

  def fetchMessagesFilesAndCopy(services: List[String], dir: File): Unit = {

    for(service <- services) {
      val bufferedSource = try {
        Source.fromFile(s"/Users/geoffwatson/hmrc/$service/conf/messages")
      } catch {
        case e:Exception => Source.fromFile(s"/Users/geoffwatson/hmrc/$service/conf/messages.en")
      }

      val fileName = s"$service"

      writeToFile(fileName, bufferedSource.getLines().toSeq, dir)
      bufferedSource.close()
    }
  }

  def readFile(dir: File): List[Message] = {
    val path = s"$dir/agents_messages.csv"
    val bufferedSource = Source.fromFile(path)
    val lines = bufferedSource.getLines().toSeq
    bufferedSource.close()
    val lb = new ListBuffer[Message]()
    for(line <- lines) {
      val lineArray: (String, String) = line.splitAt(line.indexOf('=') +1)
      if(lineArray._1.length > 1) lb.addOne(Message(lineArray._1, lineArray._2))
    }
    lb.toList
  }

  def writeToFile(filename: String, lines: Seq[String], dir: File): Unit = {
    val file = new File(s"$dir/$filename")
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      val lineArray: (String, String) = line.splitAt(line.indexOf('=') +1)
      bw.write(lineArray._1)
      if(lineArray._1.length > 1) bw.write('*')
      bw.write(lineArray._2)
      bw.newLine()
    }
    bw.close()
  }

  def writeFinal(filename: String, lines: Seq[String], dir: File): Unit = {
    val file = new File(s"$dir/$filename")
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines){
      bw.write(line)
      bw.newLine()
    }
    bw.close()
  }

  def combineMessages(dir: File): List[String] = {
    def write(outList:List[String], services: List[String]): List[String] = {
      services match {
        case Nil => outList
        case hd :: tl => {
          val file = Source.fromFile(s"$dir/$hd")

          val lines = file.getLines().toList
          val oList = outList ::: lines
          file.close()
          write(oList, tl)
        }
      }


    }
    write(List(), services)
  }

  def newDirectory(): File = {
    val now = LocalDateTime.now()
    val dirName = s"${now.getDayOfWeek.toString.toLowerCase().take(3)}_${now.getDayOfMonth()}_${now.getMonth().toString.toLowerCase().take(3)}_${now.getYear}_${now.getHour()}:${now.getMinute}:${now.getSecond()}"
    val dir: File = new File(s"/Users/geoffwatson/Downloads/messages_gatherer/$dirName")
    dir
  }

  def fetchEmailTemplates(templates: List[String], dir: File): Unit = {
    for (template <- templates) {
      val path = s"/Users/geoffwatson/hmrc/hmrc-email-renderer/app/uk/gov/hmrc/hmrcemailrenderer/templates/agent/$template"
      val bufferedSource = Source.fromFile(path)
      val fileName = s"$template"

      writeToFileEmail(fileName, bufferedSource.getLines().toSeq, dir)
      bufferedSource.close()
    }
  }

  def writeToFileEmail(filename: String, lines: Seq[String], dir: File) : Unit = {
    val file = new File(s"$dir/$filename")
    val bw = new BufferedWriter(new FileWriter(file))
    for (line <- lines) {
      bw.write(line)
      bw.newLine()
    }
    bw.write("###########")
    bw.close()
  }

  def combineEmailTemplates(): List[String] = {
    def write(outList:List[String], templates: List[String]): List[String] = {
      templates match {
        case Nil => outList
        case hd::tl => {
          val file = Source.fromFile(hd)
          val lines = file.getLines().toList
          val oList = outList ::: lines
          file.close()
          write(oList, tl)
        }
      }
    }
    write(List(), emailTemplates)
  }

}
