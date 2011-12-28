package com.gu.management.mongodb

import com.gu.management.TimingMetric

object MongoRequests extends TimingMetric("application", "mongodb", "MongoDB requests", "Captures the number of requests and the total time taken for all requests through the MongoDB driver")