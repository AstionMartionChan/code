package com.cfy

import scalikejdbc._


object CommonDBCP {
  @volatile private var isInitialized = false
  val lock = new AnyRef
  implicit var jdbcConfig: JdbcConfig  = new JdbcConfig
  try {
    Class.forName("com.mysql.jdbc.Driver")
  }
  catch {
    case e: ClassNotFoundException => {
      e.printStackTrace()
    }
  }

  //  @volatile private var isInitialized: Boolean = false

  def init(implicit jdbcConfig: JdbcConfig= new JdbcConfig) = {
    if (!isInitialized) {
      lock.synchronized {
        if (!isInitialized) {
          val settings = ConnectionPoolSettings(
            initialSize = 5,
            maxSize = 20,
            connectionTimeoutMillis = 3000L,
            validationQuery = "select 1 from dual")
          ConnectionPool.singleton(jdbcConfig.getUrl.trim, jdbcConfig.getUser.trim, jdbcConfig.getPwd.trim, settings)
          this.jdbcConfig = jdbcConfig
          val hook = new Thread {
            override def run(): Unit = {
              println("================Execute hook thread to close jdbc connection pool:" + this)
              ConnectionPool.close()
            }
          }
          sys.addShutdownHook(hook.run())
          println("=============== common dbcp2 connection pool initialized ==================================================")
          isInitialized = true
        }
      }
    }
  }

  def withPool[T](body: => T)(implicit jdbcConfig: JdbcConfig = new JdbcConfig): T = {
    CommonDBCP.init
    body
  }

}
