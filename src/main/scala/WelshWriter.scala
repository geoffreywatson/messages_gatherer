import java.io.{BufferedWriter, File, FileWriter}



import scala.io.Source

object WelshWriter {

  //read in the english file
  //for each line, lookup the welsh version of the english message.
  //write out to a file with the key and welsh message.


  def writeWelsh(welshMap: Map[String, (String, String)], service: String, folder: String): Unit = {

    val folderPath = s"/Users/geoffwatson/Downloads/messages_gatherer/$folder"
    val source = s"$folderPath/$service"
    val target = s"$folderPath/$service.we"

    val bs = Source.fromFile(new File(source))
    val bw = new BufferedWriter(new FileWriter(target))
    val lines = bs.getLines()
    for (line <- lines) {
      bw.write(parseLine(line, welshMap))
      bw.newLine()
      }
    bs.close()
    bw.close()
  }


  private def parseLine(line: String, welshMap: Map[String, (String, String)]): String = {
    val lineArray: Array[String] = line.split('*')
    if(lineArray.length >1 ) {
      val key = lineArray(0)
      val engMsg = lineArray(1)
      val welshMsg: String = welshMap.get(engMsg) match {
        case Some(s)  => s._1
        case None     => ""
      }
      s"$key$welshMsg"
    } else line
  }
}
