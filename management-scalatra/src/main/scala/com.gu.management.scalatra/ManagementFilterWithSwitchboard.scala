package com.gu.management.scalatra

import com.gu.management.switching.Switchable
import org.apache.commons.lang.StringUtils


trait ManagementFilterWithSwitchboard extends ManagementFilter {

  protected override def managementGetUrls: Map[String, () => _] = super.managementGetUrls ++ Map(
    "/management/switchboard" -> switchboardGet _
  )

  protected override def managementPostUrls: Map[String, () => _] = super.managementPostUrls ++ Map(
    "/management/switchboard" -> switchboardPost _
  )

  object logger extends Logging

  implicit val switchableOrdering: Ordering[Switchable] = Ordering[String] on { _.getWordForUrl }

  protected def switchables: List[Switchable] = List()

  private def switchboardTableRow(switchable: Switchable) = {
    <tr>
      <td>
        <a href={"?switch=" + switchable.getWordForUrl}>{ switchable.getWordForUrl }</a>
      </td>
      <td>{ switchable.getDescription }</td>
      <td style="width: 100px; text-align: center;">{
        switchable.isSwitchedOn match {
          case true => <span style="color: ForestGreen"> ON </span><input type="submit" name={ switchable.getWordForUrl } value="OFF" />
          case false => <input type="submit" name={ switchable.getWordForUrl } value="ON"/><span style="color: DarkRed"> OFF </span>
        }
      }
      </td>
    </tr>
  }

  protected def switchboardGet() = {
    val switchToShow: Option[String] = Option(request getParameter "switch") filter { !StringUtils.isEmpty(_) }
    val switchesToShow = switchToShow match {
      case Some(wordForUrl) => switchables filter { _.getWordForUrl == wordForUrl }
      case _ => switchables
    }

    <html>
      <head>
        <title>Switchboard</title>
      </head>
      <body>
        <form method="POST">
          <table border="1">
            <tr><th>Switch name</th><th>Description</th><th>State</th></tr>
            { switchesToShow.sorted map switchboardTableRow }
          </table>
        </form>
      </body>
    </html>
  }

  protected def switchboardPost() {
    for (switchable <- switchables) {
      Option(request.getParameter(switchable.getWordForUrl)) map { _.toUpperCase } match {
        case Some("ON") => switchOnWithLogging(switchable)
        case Some("OFF") => switchOffWithLogging(switchable)
        case _ =>
      }
    }

    val switchToShow: Option[String] = Option(request getParameter "switch") filter { !StringUtils.isEmpty(_) }
    redirect(request.getRequestURI + (switchToShow map { "?switch=" + _ }).getOrElse("") )
  }

  private def switchOffWithLogging(switchable: Switchable) {
    if (switchable.isSwitchedOn) {
      logger.info("Switching off " + switchable.getWordForUrl)
      switchable.switchOff
    }
  }

  private def switchOnWithLogging(switchable: Switchable) {
    if (!switchable.isSwitchedOn) {
      logger.info("Switching on " + switchable.getWordForUrl)
      switchable.switchOn
    }
  }
}