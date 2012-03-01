package com.gu.management.request

import javax.servlet.http.{ HttpServletRequest, HttpServletRequestWrapper }
import scalax.io.Resource
import scala.collection.JavaConversions._
import java.io.ByteArrayInputStream
import java.net.URLDecoder._
import javax.servlet.{ ServletRequest, ServletInputStream }

class BodyCachingRequestWrapper private (wrappedRequest: HttpServletRequest) extends HttpServletRequestWrapper(wrappedRequest) {
  val cachedBody = Resource.fromInputStream(wrappedRequest.getInputStream).bytes.toArray

  lazy val characterEncoding = Option(wrappedRequest.getCharacterEncoding).getOrElse("UTF-8")

  private def addValuesToMultiMap(parameterMap: Map[String, Array[String]], key: String, value: Array[String]) = {
    parameterMap + (
      key -> Array.concat(
        parameterMap.get(key).getOrElse(Array[String]()),
        value)
    )
  }

  lazy val formParams: Map[String, Array[String]] =
    if (wrappedRequest getContentType () startsWith ("application/x-www-form-urlencoded")) {
      new String(cachedBody, characterEncoding)
        .split('&')
        .foldLeft(
          Map[String, Array[String]]()
        ) { (parameterMap, keyValueString) =>
            keyValueString.split('=') match {
              case Array(key, value) =>
                addValuesToMultiMap(parameterMap, decode(key, characterEncoding), Array(decode(value, characterEncoding)))
              case _ => parameterMap
            }
          }
    } else {
      Map()
    }

  //in theory wrappedRequest.getParameterMap should not return parameters from the body as the body
  //has been read at this point, this may vary depending on container implementation or if there are other
  //body caching wrappers up stream
  lazy val params = wrappedRequest.getParameterMap
    .foldLeft(formParams) { (parameterMap, keyValue) =>
      addValuesToMultiMap(parameterMap, keyValue._1.asInstanceOf[String], keyValue._2.asInstanceOf[Array[String]])
    }

  override def getParameter(name: String) = params.getOrElse(name, Array.empty).headOption.getOrElse[String](null)

  override def getParameterNames = new java.util.Enumeration[String] {
    val keyIterator = params.keySet.iterator
    override def hasMoreElements = keyIterator.hasNext
    override def nextElement() = keyIterator.next
  }

  override def getParameterValues(name: String) = {
    params.get(name) match {
      case Some(values) => values.toArray
      case None => null
    }
  }

  override def getParameterMap = {
    params
  }

  override def getInputStream = {
    new ServletInputStream {
      val inputStream = new ByteArrayInputStream(cachedBody)
      def read() = inputStream.read()
      override def read(p1: Array[Byte]) = inputStream.read(p1)
      override def read(p1: Array[Byte], p2: Int, p3: Int) = inputStream.read(p1, p2, p3)
      override def skip(p1: Long) = inputStream.skip(p1)
      override def available() = inputStream.available()
      override def close() { inputStream.close() }
      override def mark(p1: Int) { inputStream.mark(p1) }
      override def reset() { inputStream.reset() }
      override def markSupported() = inputStream.markSupported()
    }
  }
}

object BodyCachingRequestWrapper {
  def apply(request: HttpServletRequest): BodyCachingRequestWrapper = {
    request match {
      case request: BodyCachingRequestWrapper => request
      case request => new BodyCachingRequestWrapper(request)
    }
  }
}