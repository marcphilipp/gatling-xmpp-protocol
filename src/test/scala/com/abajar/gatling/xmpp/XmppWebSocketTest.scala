package com.abajar.gatling.xmpp

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation

import scala.concurrent.duration._
import com.abajar.gatling.xmpp.Predef._

class XmppWebSocketTest extends Simulation {

  val xmppProtocol = xmpp.endpoint("localhost", 5280, "capulet.lit")
    .webSocketPath("/websocket")
    .secure(false)

  val scn = scenario("XMPP over WebSocket")
    .exec(xmpp("connect").connect("juliet", "secret"))
    .pause(1)
    .exec(xmpp("join room").join("balcony", "conference.capulet.lit"))
    .pause(1)
    .exec(xmpp("disconnect").disconnect())

  setUp(scn.inject(atOnceUsers(1)))
    .protocols(xmppProtocol)
    .maxDuration(1 minute)

}
