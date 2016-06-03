package com.github.synteny.faas

import java.net.{ConnectException, InetSocketAddress}

import org.scalatest.{PrivateMethodTester, Matchers}

import scala.io.Source

class ServiceTest extends org.scalatest.FlatSpec with Matchers with PrivateMethodTester {
  "Service" should "run the provided handler" in {
    def getFreePort : Int = {
      (49152 until 65535).find(Utils.testTcpPort) match {
        case Some(p) => p
        case None => throw new RuntimeException("No available ports discovered")
      }
    }

    val port = getFreePort
    val service = Service.start(new InetSocketAddress(port), 1, DummyResponder)

    val response = Source.fromURL(s"http://127.0.0.1:$port/dummy?dummy_param=none").mkString
    response shouldEqual DummyResponder.CONTENT

    service.stop()

    a [ConnectException] should be thrownBy {
      Source.fromURL(s"http://127.0.0.1:$port/dummy")
    }
  }

  "Its query string parser" should "work correctly" in {
    val parseQueryString = PrivateMethod[Map[String, String]]('parseQueryString)

    Service invokePrivate parseQueryString("field1=value1") shouldEqual Some(Map("field1" -> "value1"))
    Service invokePrivate parseQueryString("field1=value1&field2=value2;field3=value3") shouldEqual Some(
      Map("field1" -> "value1", "field2" -> "value2", "field3" -> "value3")
    )

    Service invokePrivate parseQueryString("field1value1") shouldBe None
    Service invokePrivate parseQueryString("field1=value1&field2") shouldBe None
  }
}
