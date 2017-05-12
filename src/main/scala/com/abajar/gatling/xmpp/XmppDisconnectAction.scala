package com.abajar.gatling.xmpp

import akka.actor.ActorRef
import io.gatling.core.action.{Failable, Interruptable}
import io.gatling.core.session.Expression
import io.gatling.core.validation.Validation
import io.gatling.core.session.Session
import io.gatling.core.util.TimeHelper._
import io.gatling.core.result.message.{KO, OK, Status}
import io.gatling.core.result.writer.DataWriterClient
import rocks.xmpp.core.session.XmppClient

import scala.concurrent.Future
import scala.util.{Failure, Success}

class XmppDisconnectAction(requestName: Expression[String], val next: ActorRef) extends Interruptable with Failable {
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

    def disconnect(session: Session, requestName: String) {
      val start = nowMillis
      val disconnect = Future {
        session("xmppClient").as[XmppClient].close()
      }

      val updatedSession = session.set("xmppClient", null)

      disconnect.onComplete { 
        case Success(_) =>
          val end = nowMillis
          logResult(updatedSession, requestName, OK, start, end)
          next ! updatedSession
        case Failure(e) =>
          logger.error(e.getMessage)
          val end = nowMillis
          logResult(updatedSession, requestName, KO, start, end)
          next ! updatedSession
      }
    }

    for {
      requestName <- requestName(session)
    } yield disconnect(session, requestName)
  }
}
