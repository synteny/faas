package com.github.synteny.faas

/**
  * Dummy responder for testing the service, returns "42" on any request
  */
object DummyResponder extends Responder {
  val CONTENT = "42"

  val path: String = "/dummy"

  override def getResponseBody(arguments : Map[String, String]): Either[String, String] = Right(CONTENT)
}
