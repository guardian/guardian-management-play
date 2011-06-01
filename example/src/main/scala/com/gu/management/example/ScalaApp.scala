package com.gu.management.example

import com.gu.management.Switch
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class ScalaApp extends HttpServlet {
  protected override def doPost(request: HttpServletRequest, response: HttpServletResponse) {
  }

  protected override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    TimingMetrics.requests measure {
      Switches.takeItDown whenOn {
        response.sendError(500, "Temporarily switched off!")
      }

      Switches.takeItDown whenOff {
        response.getWriter.println("Thank you for invoking this app!")
      }

    }
  }
}
