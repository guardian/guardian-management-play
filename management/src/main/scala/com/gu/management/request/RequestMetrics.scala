package com.gu.management.request

import com.gu.management.TimingMetric

object HttpRequestsTimingMetric extends TimingMetric(
    group = "application", 
    name = "http-requests", 
    title = "HTTP requests", 
    description = "HTTP requests as determined by the request logging filter")