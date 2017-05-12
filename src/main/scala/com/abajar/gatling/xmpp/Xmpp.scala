package com.abajar.gatling.xmpp

import io.gatling.core.session.Expression

class Xmpp(requestName: Expression[String]) {
  def connect(user: Expression[String], password: Expression[String]) = new XmppConnectActionBuilder(requestName, user, password)
  def join(roomName: Expression[String], domain: Expression[String]) = new XmppJoinMucActionBuilder(requestName, roomName, domain)
  def disconnect() = new XmppDisconnectActionBuilder(requestName)
}
