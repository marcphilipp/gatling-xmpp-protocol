package com.abajar.gatling.xmpp

import java.util.UUID
import javax.net.ssl.{HostnameVerifier, SSLSession}

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation._
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{KO, OK, Status}
import io.gatling.core.result.writer.DataWriterClient
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.websocket.WebSocketConnectionConfiguration

import scala.concurrent.Future
import scala.util.{Failure, Success}

class XmppConnectAction(requestName: Expression[String], user: Expression[String], password: Expression[String], val next: ActorRef, protocol: XmppProtocol) extends Interruptable with Failable {
  override def executeOrFail(session: Session): Validation[_] = {
    def logResult(session: Session, requestName: String, status: Status, started: Long, ended: Long) {
      new DataWriterClient{}.writeRequestData(
        session,
        requestName,
        started,
        ended,
        ended,
        ended,
        status
      )
    }

    def connect(session: Session, requestName: String, user: String, password: String) {
      val start = nowMillis
      val connect = Future {
        protocol match {
            case webSocketProtocol: XmppWebSocketProtocol =>
              val config = WebSocketConnectionConfiguration.builder()
                  .hostname(webSocketProtocol.address)
                  .port(webSocketProtocol.port)
                  .path(webSocketProtocol.path)
                  .secure(webSocketProtocol.secure)
                  .hostnameVerifier(new HostnameVerifier {
                    override def verify(hostname: String, sslSession: SSLSession): Boolean = true
                  })
                  .build()
              val xmppClient = XmppClient.create(webSocketProtocol.domain, config)
              val resource = "gatling-xmpp-" + UUID.randomUUID.toString
              xmppClient.connect(Jid.of(user, webSocketProtocol.domain, resource))
              xmppClient.login(user, password, resource)
              xmppClient
            case _ => ???
          }
      }

      connect.onComplete { 
        case Success(connection) =>
          val end = nowMillis
          val updatedSession = session.set("xmppClient", connection)
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        case Failure(e) =>
          val end = nowMillis
          logger.error(e.getMessage)
          logResult(session, requestName, KO, start, end)
          next ! session
      }
    }

    for {
      requestName <- requestName(session)
      user <- user(session)
      password <- password(session)
    } yield connect(session, requestName, user, password)
  }
}
