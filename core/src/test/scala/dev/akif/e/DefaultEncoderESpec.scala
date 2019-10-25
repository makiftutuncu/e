package dev.akif.e

import java.util.{HashMap => JMap}
import org.scalatest.{Matchers, WordSpec}

class DefaultEncoderESpec extends WordSpec with Matchers {
  "DefaultEncoderE" should {
    "encode an E with code" in {
      val e = E.empty.code(1)

      val expected = """{"code":1}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code and name" in {
      val e = new E(1, "test")

      val expected = """{"code":1,"name":"test"}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code, name and message" in {
      val e = new E(1, "test", "Test Message")

      val expected = """{"code":1,"name":"test","message":"Test Message"}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code, name, message and cause" in {
      val e = new E(1, "test", "Test Message", new RuntimeException("test"), null)

      val expected = """{"code":1,"name":"test","message":"Test Message","cause":"test"}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code, name, message and data" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", null, d)

      val expected = """{"code":1,"name":"test","message":"Test Message","data":{"foo":"bar"}}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code, name, message, cause and data" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", new RuntimeException("test"), d)

      val expected = """{"code":1,"name":"test","message":"Test Message","cause":"test","data":{"foo":"bar"}}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E with code, name, message, cause and data and escape quotes" in {
      val d = new JMap[String, String]
      d.put("fo\"o", "b\"ar")
      val e = new E(1, "te\"st", "Test\"Message", new RuntimeException("te\"st"), d)

      val expected = """{"code":1,"name":"te\"st","message":"Test\"Message","cause":"te\"st","data":{"fo\"o":"b\"ar"}}"""
      val actual   = DefaultEncoderE.encode(e)

      actual shouldBe expected
    }
  }
}
