package com.kingnetdc.watermelon.utils

import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import java.nio.charset.Charset
import com.esotericsoftware.kryo.io.{Output, Input}
import com.google.common.hash.Hashing
import com.kingnetdc.watermelon.utils.AppConstants._
import com.twitter.chill.EmptyScalaKryoInstantiator
import org.apache.commons.codec.binary.Base64
import org.apache.commons.io.IOUtils

object StringUtils {

    private val hashFunction = Hashing.murmur3_128()

    def hashString(str: String) = {
        hashFunction.hashBytes(str.getBytes(Charset.forName(UTF8)))
    }

    def nonEmpty(str: String) = org.apache.commons.lang3.StringUtils.isNotBlank(str)

    def deserializeObject[T](data: Array[Byte]): T = {
        val instantiator = new EmptyScalaKryoInstantiator
        val kryo = instantiator.newKryo()

        var input: Input = null
        try {
            val bais = new ByteArrayInputStream(data)
            input = new Input(bais)
            kryo.readClassAndObject(input).asInstanceOf[T]
        } finally {
            IOUtils.closeQuietly(input)
        }
    }

    def deserializeObject[T](data: String): T = {
        val instantiator = new EmptyScalaKryoInstantiator
        val kryo = instantiator.newKryo()

        var input: Input = null
        try {
            val bais = new ByteArrayInputStream(new Base64().decode(data))
            input = new Input(bais)
            kryo.readClassAndObject(input).asInstanceOf[T]
        } finally {
            IOUtils.closeQuietly(input)
        }
    }

    def md5AsByteArray(key: String): Array[Byte] = {
        Hashing.md5().hashBytes(key.getBytes(Charset.forName(UTF8))).asBytes()
    }

    def md5(key: String): String = {
        Hashing.md5().hashBytes(key.getBytes(Charset.forName(UTF8))).toString
    }

    def md5(key: Long) = {
        Hashing.md5().hashLong(key).asBytes()
    }

    def serializeObject(any: Any) = {
        val instantiator = new EmptyScalaKryoInstantiator
        val kryo = instantiator.newKryo()

        var output: Output = null
        try {
            val baos = new ByteArrayOutputStream()
            output = new Output(baos)
            kryo.writeClassAndObject(output, any)
            output.flush()

            baos.toByteArray()
        } finally {
            IOUtils.closeQuietly(output)
        }
    }

}
