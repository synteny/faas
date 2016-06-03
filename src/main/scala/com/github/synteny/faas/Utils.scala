package com.github.synteny.faas

import java.io.IOException
import java.net.ServerSocket

/**
  * Shared procedures
  */
object Utils {
  def testTcpPort(port : Int) : Boolean = {
    var socket : Option[ServerSocket] = None
    try {
      socket = Some(new ServerSocket(port))
      true
    }
    catch {
      case _ : IOException => false
    }
    finally {
      try {
        for (v <- socket) v.close()
      }
      catch { case _ : Throwable =>
        /* this should never happen */
      }
    }
  }
}
