package com.gu.management

import management.ManagementFactory

import collection.JavaConversions._


object JvmMetrics {
  lazy val all = List(numThreads, totalThreads) ::: gcRates

  lazy val numThreads = new Metric {
    def asJson = StatusMetric(
      group = "jvm",
      name = "num_threads",
      `type` = "guage",
      title = "Number of active threads",
      description = "Number of threads currently active as reported by the jvm",
      value = Some(ManagementFactory.getThreadMXBean.getThreadCount.toString)
    )
  }

  lazy val totalThreads = new Metric {
    def asJson = StatusMetric(
      group = "jvm",
      name = "total_threads",
      `type` = "counter",
      title = "Total started threads",
      description = "Threads started since the application started as reported by the jvm",
      value = Some(ManagementFactory.getThreadMXBean.getTotalStartedThreadCount.toString)
    )
  }

  lazy val gcRates = ManagementFactory.getGarbageCollectorMXBeans.toList map { gc =>
    new Metric {
      def asJson = StatusMetric(
        group = "jvm",
        name = "gc_" + gc.getName.toLowerCase.replace(' ', '_'),
        `type` = "timer",
        title = "GC " + gc.getName,
        description = "Collection rates for the " + gc.getName + " garbage collector",
        count = Some(gc.getCollectionCount.toString),
        totalTime = Some(gc.getCollectionTime.toString)
      )
    }
  }
}
