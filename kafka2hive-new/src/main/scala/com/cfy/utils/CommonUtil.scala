package com.cfy.utils

import com.cfy.log.Logging

/**
  * Created by IntelliJ IDEA.
  * User: Leo_Chan
  * Date: 2018/11/26
  * Time: 13:34
  * Work contact: Astion_Leo@163.com
  */


object CommonUtil extends Logging{

  type Closeable = {def close()}

  def safeRelease[R <: Closeable, A](resource: R)(f: R => A): A = {
    try {
      f(resource)
    } catch {
      case e: Exception => throw new Exception("safe release fail ", e)
    } finally {
      resource.close()
    }
  }

}
