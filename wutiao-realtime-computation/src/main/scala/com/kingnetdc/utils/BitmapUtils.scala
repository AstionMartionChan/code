package com.kingnetdc.utils

import java.io.{ByteArrayInputStream, DataInputStream, ByteArrayOutputStream, DataOutputStream}
import org.apache.commons.io.IOUtils
import org.roaringbitmap.{RoaringBitmap => BitMap}

/**
 * Created by zhouml on 04/08/2018.
 */
object BitmapUtils extends Serializable {

    def toByteArray(bitmap: BitMap): Array[Byte] = {
        bitmap.runOptimize()

        var dos: DataOutputStream = null
        try {
            val baos = new ByteArrayOutputStream(bitmap.serializedSizeInBytes())
            dos = new DataOutputStream(baos)
            bitmap.serialize(dos)
            baos.toByteArray()
        } finally {
            IOUtils.closeQuietly(dos)
        }
    }

    def asBitMap(byteArr: Array[Byte]) = {
        val bitmap = new BitMap()
        var dis: DataInputStream = null

        try {
            dis = new DataInputStream(new ByteArrayInputStream(byteArr))
            bitmap.deserialize(dis)
            bitmap
        } finally {
            IOUtils.closeQuietly(dis)
        }
    }

}
