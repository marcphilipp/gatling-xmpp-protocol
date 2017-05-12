package com.abajar.gatling.xmpp

class XmppWebSocketProtocol(val address: String, val port: Int, val domain: String, val path: String, val secure: Boolean) extends XmppProtocol(address, port, domain) {
}
