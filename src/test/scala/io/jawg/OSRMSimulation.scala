/*
 * Copyright 2015 eBusiness Information
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jawg

import java.util.Random

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import scala.concurrent.duration._


object Parameters {

  val propertiesFromSystem = ConfigFactory.systemProperties()
  val propertiesFromFile = ConfigFactory.load("parameters.properties")
  val properties = propertiesFromSystem.withFallback(propertiesFromFile)

  // Url of the server to stress test
  val OSRM_URLS = properties.getString("server.url").trim().split(",").toList
  // Profile to use with osrm
  val OSRM_PROFILE = properties.getString("osrm.profile").trim()

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // sample.csv contains an example of the format used.
  val CSV_FILE = properties.getString("simulation.regions")

  // File to load containing the region rectangles where users will choose their initial latitudes and longitudes.
  // sample.csv contains an example of the format used.
  val SEED_FILE = properties.getString("simulation.seeds")

  // Amount of users. Users will be dispatched as equally as possible across regions.
  val USERS = properties.getString("simulation.users.count").toInt

  // Users amount can be ramped up over this duration in seconds
  val RAMP_TIME = properties.getString("simulation.users.ramp.time").toInt.seconds

  // Note :
  // The time units can be specified, for instance 1.minute, 1000.millis, etc
}

class OSRMSimulation extends Simulation {

  import Parameters._

  val continentMap = csv(CSV_FILE).records.groupBy(record => record("Continent"))
  val httpProtocol = http.baseURLs(OSRM_URLS)

  implicit class PositiveMod(a: Int) {

    // a mod b
    def %%(b: Int) = (a % b + b) % b
  }

  def randomLatitudes: Expression[Session] = { session =>
    val seed = session("seed").as[String].toLong
    val rand = new Random(seed)
    val continent = session("Continent").as[String]
    val regionsOfContinent = continentMap(continent)
    val endRegion = regionsOfContinent(rand.nextInt() %% regionsOfContinent.length)

    val startLatMin = session("LatMin").as[String].toDouble
    val startLatMax = session("LatMax").as[String].toDouble
    val startLngMin = session("LngMin").as[String].toDouble
    val startLngMax = session("LngMax").as[String].toDouble

    val endLatMin = endRegion("LatMin").toDouble
    val endLatMax = endRegion("LatMax").toDouble
    val endLngMin = endRegion("LngMin").toDouble
    val endLngMax = endRegion("LngMax").toDouble

    val startLng = rand.nextDouble() * (startLngMax - startLngMin) + startLngMin
    val startLat = rand.nextDouble() * (startLatMax - startLatMin) + startLatMin

    val endLng = rand.nextDouble() * (endLngMax - endLngMin) + endLngMin
    val endLat = rand.nextDouble() * (endLatMax - endLatMin) + endLatMin

    session
      .set("startLat", startLat)
      .set("startLng", startLng)
      .set("endLat", endLat)
      .set("endLng", endLng)
      .set("endRegion", endRegion("Region"))
      .set("profile", OSRM_PROFILE)
  }

  val scn = scenario("OSRMSimulation")
    .feed(csv(CSV_FILE).circular)
    .feed(csv(SEED_FILE).circular)
    .exec(randomLatitudes)
    .exec(
      http("${Region} to ${endRegion}").get("/route/v1/${profile}/${startLng},${startLat};${endLng},${endLat}")
        .queryParam("overview", false)
        .check(status.is(200))
    )

  setUp(scn.inject(rampUsers(USERS) over RAMP_TIME))
    .protocols(httpProtocol)
}
