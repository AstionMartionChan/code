package com.cfy.utils
import scalikejdbc._
import scalikejdbc.config._


object JdbcHandler {

  @volatile private var isInitialized = false
  private val lock = new AnyRef


  def init = {
    if (!isInitialized){
      lock.synchronized{
        if (!isInitialized){
          DBs.setupAll()

          val hook = new Thread{
            override def run(): Unit = {
              println("-----------------------------Execute hook thread to close jdbc connection poll ---------------------------------" + this)
              DBs.closeAll()
            }
          }

          sys.addShutdownHook(hook.run)

          println("-----------------------jdbc connection poll initialized success------------------------------" + this)
          isInitialized = true
        }
      }
    }
  }


  def select[T](sql: String)(f:WrappedResultSet => T) = {
    init
    DB readOnly { implicit session =>
      SQL(sql).map(f).list.apply
    }
  }

  def insertOrUpdate(sql: String, batchParam: Seq[Seq[Any]]) = {
    init
    DB localTx { implicit session =>
      SQL(sql).batch(batchParam: _*).apply()
    }
  }






}
