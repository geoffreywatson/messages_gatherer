import FileReader._
import CompareVersions._
import WelshFileReader._
import WelshWriter._

import scala.collection.immutable.HashMap

object Main extends App {

  def run(): Unit = {
//    val dir = newDirectory()
//    dir.mkdir()
//    fetchMessagesFilesAndCopy(services, dir)
//    val allMessages = combineMessages(dir)
//    writeFinal("agents_messages.csv", allMessages, dir)
//    fetchEmailTemplates(emailTemplates, dir)
//    val allEmailTemplates = combineEmailTemplates()
//    writeFinal("agents_email_templates.csv", allEmailTemplates, dir)
    //CompareVersions("sent_to_welsh_group_jan")

    //val result = makeWelshList("welsh_pt1.txt")



    //val welshMap = toWelshMap(result)

   //writeWelsh(welshMap, "agent-services-account-frontend", "tue_11_feb_2020_9:40:31")

      //println(welshMap.get("Use this service to update your client's VAT registration status, business name (if they are a limited company), principal place of business and VAT stagger."))

    import ExcelReader._

    val pt1 = "Cell 1-500 Agent services for new translation Cymraeg-Welsh (1).xlsx"
    val pt2 = "Cell 500-1000 Agent services for new translation CYMRAEG.xlsx"
    val pt3 = "Cell 1001-1500 Agent services for new translation_WELSH.xlsx"
    val pt4 = "Cell 1501-1962 Agent service for new translation - Translation.xlsx"
    val pt5 = "Source file sent for translation #4769 (2).xlsx"
    val folder = "mon_2_mar_2020_9:38:29"
    val welshMap = makeWelshMapFromExcel(pt1,pt2,pt3,pt4, pt5)
    writeWelshMessagesFile(folder,"agent-invitations-frontend", welshMap)

//    val queryResult = welshMap.get("<a href=\"{0}\" id=\"{1}\" >Copy across your existing Self Assessment and VAT clients to this account</a>")
//
//    println(queryResult)

//    def messagesFile(folder: String) = s"/Users/geoffwatson/Downloads/messages_gatherer/$folder/agents_messages.csv"
//
//    val processedFile = readMasterWriteWelsh(messagesFile("mon_10_feb_2020_14:34:6"), welshMap)

    //println(processedFile.count(_.welshMsgWithKey.isDefined))

    //println(processedFile.size)

    //println(s"welsh map size is ${welshMap.size}")

  }
  run()
}
