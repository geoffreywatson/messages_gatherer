import java.io.{BufferedWriter, File, FileWriter}

import org.apache.poi.ss.usermodel.{Cell, CellType, Row, Sheet, WorkbookFactory}

import scala.collection.mutable.ListBuffer
import scala.io.Source

object ExcelReader {

  def makeWelshMapFromExcel(filenames: String*): Map[String, String] = {
    val lb = new ListBuffer[(String, String)]()
    for(files <- filenames) {

      val file = s"/Users/geoffwatson/Downloads/messages_gatherer/$files"
      val f = new File(file)
      val workbook = WorkbookFactory.create(f)
      val sheet: Sheet = workbook.getSheetAt(0) //results in first sheet
      val rowIterator = sheet.rowIterator()

      while(rowIterator.hasNext) {

        val row: Row = rowIterator.next()
        if (row != null) {
          val enCell = row.getCell(0)
          val cyCell = row.getCell(1)
          if (enCell != null && cyCell != null) {
            val enMsg = extractCellValue(enCell)
            val cyMsg = extractCellValue(cyCell)
            lb.addOne(enMsg, cyMsg)
          } else {
            //println(s"null cell at $i is $enCell or $cyCell")
          }
        } else {
          //println(s"null row at $i is $row")
        }
      }
    }
    lb.toMap
  }

  private def extractCellValue(cell: Cell): String = cell.getCellTypeEnum match {
    case CellType.STRING => cell.getStringCellValue
    //case CellType.FORMULA => cell.getCellFormula
    //case CellType.NUMERIC => cell.getNumericCellValue.toString
    case CellType.BLANK => ""
    case _ => throw new Exception(s"got a cell of type ${cell.getCellTypeEnum}")
  }


  def writeWelshMessagesFile(folder: String, fileForService: String, welshMap: Map[String, String]): Unit = {
    val path = s"/Users/geoffwatson/Downloads/messages_gatherer/$folder"
    val target = s"$path/$fileForService.welsh"
    val source = s"$path/$fileForService"
    val bs = Source.fromFile(source)
    val bw = new BufferedWriter(new FileWriter(target))
    val lines = bs.getLines()
    for (line <- lines) {
      bw.write(parseLine(line, welshMap))
      bw.newLine()
    }
    bs.close()
    bw.close()
  }

  case class ParsedResult(line: String, key: String,  welshResult: Option[String])

  private def parseLine(line: String, welshMap: Map[String,String]): String = {
    val lineArray: Array[String] = line.split('*')
    if(lineArray.length >1 ) {
      val key = lineArray(0)
      val engMsg = lineArray(1)
      val welshMsg: Option[String] = welshMap.get(engMsg) match {
        case Some(s)  => {
          Some(s)
        }
        case None     => {
          if(engMsg.contains('’')) {
            val modLineEn = engMsg.replace('’','\'')
             welshMap.get(modLineEn) match {
              case Some(s) => {
                Some(s.replace('\'','’'))
              }
              case None => {
                println(s"$line")
                None
              }
            }
          } else {
            println(s"$line")
            None
          }
        }
      }
      s"$key${welshMsg.getOrElse("")}"
    } else line
  }
}
