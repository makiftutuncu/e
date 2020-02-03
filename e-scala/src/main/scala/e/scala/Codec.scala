package e.scala

import e.{AbstractCodec, AbstractDecoder, AbstractEncoder}

trait Codec[A] extends AbstractCodec[E, A]

trait Decoder[IN] extends AbstractDecoder[IN, E]

trait Encoder[OUT] extends AbstractEncoder[E, OUT]
