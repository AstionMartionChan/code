package com.kingnetdc.watermelon.utils

import scala.util.{Failure, Success, Try}

object CommonUtils extends Logging {

    def safeRelease[S <: AutoCloseable, R](resource: S)(handler: S => R)(
        cleanUp: S => Unit = (s: S) => s.close
    ): Try[R] = {
        try {
            Success(handler(resource))
        } catch {
            case e: Exception =>
                logger.error("Error during resource handler", e)
                Failure(e)
        } finally {
            if (resource != null) {
                cleanUp(resource)
            }
        }
    }

}
