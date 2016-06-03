package com.github.synteny.faas

import java.util.Calendar

/**
  * Global logger used across threads.
  */
object Logger {
  def write(message : String) = {
    this.synchronized {
      println(s"[${Calendar.getInstance.getTime}] $message")
    }
  }
}
