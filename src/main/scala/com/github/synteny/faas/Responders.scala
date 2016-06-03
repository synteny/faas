package com.github.synteny.faas

import scala.util.{Failure, Success, Try}

/**
  * Responders provide response generation logic given query arguments
  */
trait Responder {
  /** URL path associated with this handler */
  def path : String

  /** Generates response specific to the submitted request, to be defined by inheritors */
  def getResponseBody(arguments : Map[String, String]) : Either[String, String]
}

/**
  * Request handler for factorial calculation queries
  */
object FactorialResponder extends Responder {
  val path: String = "/calc"

  private val ARGNAME = "n"

  override def getResponseBody(arguments : Map[String, String]): Either[String, String] = {
    if (arguments contains ARGNAME) {
      Try { arguments(ARGNAME).toInt } match {
        case Success(n) =>
          if (n >= 0) Right(factorial(n).toString)
          else Left(s"Argument out of range: $n")
        case Failure(_) => Left(s"Could not parse ${arguments(ARGNAME)}")
      }
    }
    else Left(s"$ARGNAME not found among arguments")
  }

  private def factorial(n: Int) : BigInt = (1 to n).foldLeft(BigInt(1)){ case (acc, num) => acc * num }
}
