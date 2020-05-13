sealed trait VersionDiffError{
  val errorMessage: String
}

case class Message(key: String, EnMsgTxt: String, WelshMsgTxt: Option[String] = None)

case class ProcessedMessage(key: String, EnMsgText: String, welshMsgWithKey: Option[(String, String)] = None)



case class MessageResult(oldMessages: List[Message], newMessages: List[Message])

case class KeyDiffUp(message: Message) extends VersionDiffError {
  val errorMessage: String = s"message with key ${message.key} not found in earlier version."
}

case class KeyDiffDown(message: Message) extends VersionDiffError {
  val errorMessage: String = s"message with key ${message.key} not found in new version."
}

case class KeyChangeOnly(keyDiffUp: KeyDiffUp, keyDiffDown: KeyDiffDown) extends VersionDiffError {
  val errorMessage: String = s"message with key ${keyDiffUp.message.key} in new version is identical to ${keyDiffDown.message.key}."
}

case class MessageDiff(message1: Message, message2: Message) extends VersionDiffError {
  require(message1.key == message2.key)
  val errorMessage: String = s"message text with key ${message1.key} has changed"
  val messageText: String = s"[${message1.EnMsgTxt}] [${message2.EnMsgTxt}]"
}

