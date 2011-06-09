package com.gu.management.logback

import com.gu.management.{Postable, HtmlManagementPage}
import javax.servlet.http.HttpServletRequest
import collection.JavaConverters._
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Logger, Level, LoggerContext}
import xml.{NodeSeq, Text}

class LogbackLevelPage extends HtmlManagementPage with Postable {
  val myLogger = LoggerFactory.getLogger(getClass)

  val path = "/management/logback"

  private val levels = List(
    "ERROR" -> Level.ERROR, "WARN" -> Level.WARN, "INFO" -> Level.INFO,
    "DEBUG" -> Level.DEBUG, "TRACE" -> Level.TRACE, "OFF" ->Level.OFF,
    "(default)" -> null)

  // the above is a list because I care about order in dropdowns
  private val levelMap = levels.toMap



  def body(r: HttpServletRequest) = LoggerFactory.getILoggerFactory match {
    case logback: LoggerContext =>
      <form method="POST">
        <input type="submit" value="update" />
        <table border="1">
          <tr>
            <th>Level</th><th>Effective Level</th><th></th>
          </tr>
          {
            for (logger <- logback.getLoggerList.asScala) yield {
              <tr>
                <td>{logger.getName}</td>
                <td>{logger.getEffectiveLevel}</td>
                <td>
                  {dropDown(logger)}
                  <input type="submit" value="update all" />
                </td>
              </tr>
            }
          }
        </table>
        <input type="submit" value="update" />
      </form>
    case other =>
      <p>Expected a logback LoggerContext but found a {other.getClass}; are you sure you're using logback?</p>
  }

  private def dropDown(logger: Logger) =
    <select name={logger.getName}>
      {
        for ( (value, level) <- levels) yield {
          <option value={value}
                  selected={ if (logger.getLevel == level) Some(Text("selected")) else None }>{value}</option>
        }
      }
    </select>

  def title = "Logback Configuration"

  def post(r: HttpServletRequest) = {
    myLogger.info("Processing POST...")

    val logback = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    for {
      logger <- logback.getLoggerList.asScala
      param <- Option(r.getParameter(logger.getName))
      level <- levelMap.get(param)
    } {
      myLogger.info("updating %s -> %s" format (logger.getName, level))
      logger.setLevel(level)
    }
  }


}

/*
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.OutputStreamWriter
import java.util._

object Log4JManagerServlet {
  private final val MANAGEMENT_PAGE_HEAD = "<html>\n" + "<head><title>Manage log4j levels</title></head>\n" + "<body>\n" + "\t<form method=\"POST\">\n" + "\t\t<input type=\"submit\" value=\"update\" />\n" + "\n" + "<table>\n" + "\t<tr>" + "<th>Level</th>" + "<th>Effective Level</th>" + "<th></th>" + "<th>Logger</th>" + "</tr>"
  private final val MANAGEMENT_PAGE_FOOT = "</table>\n" + "\t\t<input type=\"submit\" value=\"update\" />\n" + "\t</form>\n" + "</body>\n" + "</html>"
  private final val LOGGER_TABLE_ROW: = "<tr>" + "<td><select name=\"%1$s\">%2$s</select></td>" + "<td>%3$s</td>" + "<td><input type=\"submit\" value=\"update\" /></td>" + "<td>%1$s</td>" + "</tr>"
  private final val levels: Array[Nothing] = Array[Nothing](Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE, Level.ALL)
}

class Log4JManagerServlet extends HttpServlet {
  @Override protected def doGet(request: Nothing, response: Nothing): Unit = {
    response.setContentType("text/html")
    var fileWriter: OutputStreamWriter = new OutputStreamWriter(response.getOutputStream)
    fileWriter.write(MANAGEMENT_PAGE_HEAD)
    @SuppressWarnings(Array("unchecked")) var loggers: Nothing = sortLoggers(LogManager.getCurrentLoggers)
    for (logger <- loggers) {
      fileWriter.write(String.format(LOGGER_TABLE_ROW, logger.getName, generateOptionsFor(logger), logger.getEffectiveLevel))
    }
    fileWriter.write(MANAGEMENT_PAGE_FOOT)
    fileWriter.flush
  }

  private def sortLoggers(currentLoggers: Nothing): Nothing = {
    var loggers: Nothing = new Nothing
    while (currentLoggers.hasMoreElements) {
      loggers.add(currentLoggers.nextElement)
    }
    Collections.sort(loggers, new Nothing {
      @Override def compare(logger1: Nothing, logger2: Nothing): Int = {
        return logger1.getName.compareTo(logger2.getName)
      }
    })
    return loggers
  }

  private def generateOptionsFor(logger: Nothing): Nothing = {
    var builder: Nothing = new Nothing("<option value=\"DEFAULT\" />\n")
    for (level <- levels) {
      builder.append(String.format("<option value=\"%1$s\" %2$s >%1$s</option>\n", level.toString, generatedSelectedStringFor(logger, level)))
    }
    return builder.toString
  }

  private def generatedSelectedStringFor(logger: Nothing, level: Nothing): Nothing = {
    return if (level.equals(logger.getLevel)) "selected=\"true\"" else ""
  }

  @Override protected def doPost(request: Nothing, response: Nothing): Unit = {
    @SuppressWarnings(Array("unchecked")) var loggers: Nothing = LogManager.getCurrentLoggers
    while (loggers.hasMoreElements) {
      var logger: Nothing = loggers.nextElement
      var newLogLevel: Nothing = request.getParameter(logger.getName)
      if (StringUtils.isNotEmpty(newLogLevel)) {
        if ("DEFAULT".equals(newLogLevel)) {
          logger.setLevel(null)
        }
        else {
          logger.setLevel(Level.toLevel(newLogLevel))
        }
      }
    }
    doGet(request, response)
  }
}
*/