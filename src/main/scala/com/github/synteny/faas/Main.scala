package com.github.synteny.faas

import java.net.InetSocketAddress

import jline.console.ConsoleReader

object Main extends App {
  if (Utils.testTcpPort(PORT)) {
    val service = Service.start(new InetSocketAddress(PORT), 1, FactorialResponder)

    Logger.write("Press 'q' to quit")

    val con = new ConsoleReader()
    while ( con.readCharacter() != 'q' ) {}

    Logger.write("Quitting...")
    service.stop()
  }
  else {
    Logger.write(s"Port $PORT occupied.")
  }
}
