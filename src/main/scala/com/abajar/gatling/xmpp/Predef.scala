package com.abajar.gatling.xmpp

import io.gatling.core.session.Expression

object Predef {
  val xmpp = XmppProtocolBuilder

  implicit def xmppBuilderToProtocol(builder: XmppProtocolBuilder): XmppProtocol = builder.build()
  implicit def xmppWebSocketBuilderToProtocol(builder: XmppWebSocketProtocolBuilder): XmppProtocol = builder.build()

  def xmpp(requestName: Expression[String]) = new Xmpp(requestName)
}
