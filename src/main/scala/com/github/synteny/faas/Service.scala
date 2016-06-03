package com.github.synteny.faas

import java.net.InetSocketAddress

import com.sun.net.httpserver.HttpServer

import java.net.URLDecoder.{decode => urlDecode}

import com.sun.net.httpserver.{HttpExchange, HttpHandler}

import com.github.synteny.faas.Service.HttpRequestHandler

/**
  * Manages an HttpServer instance, binding a provided HttpHandler to its specified path.
  */
class Service private (addr : InetSocketAddress, maxSessions : Int, responder : Responder) {
  private val server = HttpServer.create(addr, maxSessions)

  server.createContext(responder.path, new HttpRequestHandler(responder))
  server.start()
  Logger.write("Server started")

  def stop() : Unit = {
    server.stop(SERVICE_STOP_DELAY)
    Logger.write("Server stopped")
  }
}

object Service {
  def start(addr: InetSocketAddress, maxSessions: Int, handler: Responder): Service = {
    new Service(addr, maxSessions, handler)
  }

  /** HttpHandler implementation relying on Responders to provide response logic */
  private class HttpRequestHandler(responder : Responder) extends HttpHandler {
    val HTTP_OK = 200
    val HTTP_ERR = 503

    override def handle(exch: HttpExchange): Unit = {
      val query = urlDecode(exch.getRequestURI.getQuery, "UTF8")
      parseQueryString(query) match {
        case Some(args) =>
          responder.getResponseBody(args) match {
            case Right(cnt) =>
              exch.sendResponseHeaders(HTTP_OK, 0)
              val resp = exch.getResponseBody
              resp.write(cnt.getBytes)
              resp.close()
              Logger.write(s"Success. Response: $cnt")
            case Left(err) =>
              exch.sendResponseHeaders(HTTP_ERR, 0)
              Logger.write(s"Failure. $err")
          }
        case None =>
          Logger.write(s"Malformed query string: $query")
      }

      exch.close()
    }
  }

  /** Parses URL query string to a map of field-value pairs */
  private def parseQueryString(query : String) : Option[Map[String, String]] = {
    val fieldValue = "([^;&=]+)=([^;&=]*)".r

    Some(
      query.split(Array(';', '&')).map{
        case fieldValue(field, value) => (field, value)
        case malformed => return None
      }.toMap
    )
  }
}