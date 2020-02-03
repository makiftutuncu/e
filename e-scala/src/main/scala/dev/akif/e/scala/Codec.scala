package dev.akif.e.scala

import dev.akif.e.{AbstractCodec, AbstractDecoder, AbstractEncoder}

trait Decoder[IN] extends AbstractDecoder[IN, E]

trait Encoder[OUT] extends AbstractEncoder[E, OUT]

trait Codec[IN, OUT] extends AbstractCodec[E, IN, OUT]
