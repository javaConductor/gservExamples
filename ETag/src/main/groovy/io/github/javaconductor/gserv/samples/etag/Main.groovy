
package io.github.javaconductor.gserv.samples.etag

import io.github.javaconductor.gserv.*
import io.github.javaconductor.gserv.plugins.*
import io.github.javaconductor.gserv.utils.Encoder

class Main {

  static public void main(String[] args) {
    println "Main started!!"
    def pluginMgr = PluginMgr.instance()

    def asMap = { thing ->
      thing.class.declaredFields { !it.synthetic }.collectEntries {
        [(it.name): thing."$it.name"]
      }
    }

    def gserv = new GServ();
    gserv.plugins {
//           plugin("eventLogger", ["url":"/log"])
      plugin("caching", [:])
      plugin("compression", [:])
    }.http {
      useResourceDocs(true)
      get("/", file("text/plain", "data/rfile.txt"))
      strongETag( "/*"){exch, data ->
        //MD5 it
        Encoder.md5WithBase64(data)
      }
    }.start(54321)
  }//main
}
