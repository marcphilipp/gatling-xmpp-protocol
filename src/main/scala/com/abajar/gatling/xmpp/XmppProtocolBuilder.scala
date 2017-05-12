package com.abajar.gatling.xmpp

object XmppProtocolBuilder {
  def endpoint(address: String, port: Int, domain: String) = XmppProtocolBuilder(address, port, domain)
}

case class XmppProtocolBuilder(address: String, port: Int, domain: String) {
  def build() = XmppProtocol(address, port, domain)
  def webSocketPath(path: String) = XmppWebSocketProtocolBuilder(address, port, domain, path)
}

case class XmppWebSocketProtocolBuilder(address: String, port: Int, domain: String, path: String, var secure: Boolean = false) {
  def secure(secure: Boolean): XmppWebSocketProtocolBuilder = {
    this.secure = secure
    this
  }
  def build() = new XmppWebSocketProtocol(address, port, domain, path, secure)
}
