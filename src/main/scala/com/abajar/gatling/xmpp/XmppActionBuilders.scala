package com.abajar.gatling.xmpp

import akka.actor.ActorDSL._
import akka.actor.ActorRef
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.Protocols
import io.gatling.core.session.Expression

class XmppConnectActionBuilder(requestName: Expression[String], user: Expression[String], password: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppConnect"))(new XmppConnectAction(requestName, user, password, next,
    protocols.getProtocol[XmppWebSocketProtocol].getOrElse(throw new UnsupportedOperationException("XmppBosh Protocol wasn't registered"))))
}

class XmppDisconnectActionBuilder(requestName: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppDisconnect"))(new XmppDisconnectAction(requestName, next))
}

class XmppJoinMucActionBuilder(requestName: Expression[String], roomName: Expression[String], domain: Expression[String]) extends ActionBuilder {
  override def build(next: ActorRef, protocols: Protocols): ActorRef = actor(actorName("xmppJoin"))(new XmppJoinMucAction(requestName, next, roomName, domain))
}
