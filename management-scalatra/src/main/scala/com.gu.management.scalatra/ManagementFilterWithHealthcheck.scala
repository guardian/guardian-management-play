package com.gu.management.scalatra

import com.gu.management.switching.Switchable
import com.gu.management.healthcheck.HealthcheckEnableSwitch

trait ManagementFilterWithHealthcheck extends ManagementFilterWithSwitchboard {

  val healthCheckSwitch = new HealthcheckEnableSwitch

  protected override def switchables: List[Switchable] = healthCheckSwitch :: super.switchables

  protected override def managementGetUrls: Map[String, () => _] = super.managementGetUrls ++ Map(
    "/management/healthcheck" -> switchedHealthcheck _
  )

  private def switchedHealthcheck() = healthCheckSwitch.isSwitchedOn match {
      case false =>
        response setStatus 503
        healthCheckSwitch.getHealthcheckNotEnabledMessage

      case true => healthcheckFailures() match {
        case Nil => "OK"

        case failures  =>
          response setStatus 500
          "HEALTHCHECK FAILED:\n" + (failures map { "\t-> " + _ } mkString "\n")
      }
    }

  protected def healthcheckFailures(): List[String]
}