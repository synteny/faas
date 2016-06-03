package com.github.synteny.faas

import org.scalatest.{EitherValues, Matchers, FlatSpec}

class FactorialResponderTest extends FlatSpec with Matchers with EitherValues {
  "FactorialResponder" should "calculate factorial" in {
    FactorialResponder.getResponseBody(Map("wrong" -> "0")) should be ('left)
    FactorialResponder.getResponseBody(Map("n" -> "-1")) should be ('left)
    FactorialResponder.getResponseBody(Map("n" -> "0")).right.value shouldBe "1"
    FactorialResponder.getResponseBody(Map("n" -> "1")).right.value shouldBe "1"
    FactorialResponder.getResponseBody(Map("n" -> "4")).right.value shouldBe "24"
    FactorialResponder.getResponseBody(Map("n" -> "42")).right.value shouldBe "1405006117752879898543142606244511569936384000000000"
  }
}
