package com.gu.management.servlet

import com.gu.management.{ HttpRequest, ManagementPage }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse, HttpServletRequestWrapper }
import javax.servlet.{ ServletResponse, ServletRequest, FilterChain }
import org.scalatest
import scalatest.FlatSpec
import scalatest.matchers.ShouldMatchers
import scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import scala.collection.JavaConversions._

abstract class FakeHttpRequest extends HttpServletRequest { override def getParameterMap: java.util.Map[String, String] }

class HttpFilterTest extends FlatSpec with ShouldMatchers with MockitoSugar {
  "ManagementFilter" should "pass onto page if path matches" in {
    val testPage = mock[ManagementPage]
    val mockRequest = mock[FakeHttpRequest]
    val mockResponse = mock[HttpServletResponse]
    val finalChain = mock[FilterChain]
    val testFilter = new ManagementFilter {
      val userProvider = null
      val applicationName = "foo"
      val pages = testPage :: Nil
    }

    when(mockRequest.getMethod) thenReturn "GET"
    when(mockRequest.getParameterMap) thenReturn Map.empty[String, String]
    when(testPage.path) thenReturn "/foo"
    when(testPage.needsAuth) thenReturn false

    testFilter.doHttpFilter(mockRequest, mockResponse, finalChain)
    verify(testPage).get(ServletHttpRequest(mockRequest))
    verifyNoMoreInteractions(finalChain)
  }

}
