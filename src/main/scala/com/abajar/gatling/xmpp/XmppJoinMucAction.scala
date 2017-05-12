package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{KO, OK, Status}
import io.gatling.core.result.writer.DataWriterClient
import rocks.xmpp.addr.Jid
import rocks.xmpp.core.session.XmppClient
import rocks.xmpp.extensions.muc.MultiUserChatManager

import scala.concurrent.Future
import scala.util.{Failure, Success}

class XmppJoinMucAction(requestName: Expression[String], val next: ActorRef, roomName: Expression[String], domain: Expression[String]) extends Interruptable with Failable {
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

    def join(session: Session, requestName: String, roomName: String, domain: String) {
      val start = nowMillis
      val join = Future {
        val xmppClient = session("xmppClient").as[XmppClient]
        val mucm = xmppClient.getManager(classOf[MultiUserChatManager])
        val service = mucm.createChatService(Jid.of(domain))
        service.createRoom(roomName)
      }

      join.onComplete {
        case Success(room) =>
          val updatedSession = session.set("room", room)
          val end = nowMillis
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        case Failure(e) =>
          logger.error(e.getMessage)
          val end = nowMillis
          logResult(session, requestName, KO, start, end)
          next ! session
      }
    }

    for {
      requestName <- requestName(session)
      roomName <- roomName(session)
      domain <- domain(session)
    } yield join(session, requestName, roomName, domain)
  }
}
