
package io.github.javaconductor.gserv.samples.etag

import io.github.javaconductor.gserv.*
import io.github.javaconductor.gserv.plugins.*
import io.github.javaconductor.gserv.utils.Encoder
import net.sf.json.JSONObject

class Main {

  static public void main(String[] args) {
    println "Main started!!"

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
      useResourceDocs( true )

      get("/", file("text/plain", "data/rfile.txt"))

      // This will create a Base64 String for the eTag
      strongETag( "/"){exch, data ->
        //MD5 it
        Encoder.md5WithBase64(data)
      }

      // This will create a Base64 String for the eTag
      weakETag( "/answers/*"){context, actionArgs  ->
        //MD5 it
        def data =  actionArgs.join('.').bytes
        Encoder.md5WithBase64(data)
      }

      get('/answers/:op/:num1:Number/:num2:Number'){ op, num1, num2 ->
        def ans
        switch(op){
          case 'add': ans = num1 + num2; break;
          case 'multiply':  ans = (num1 ) * (num2 ); break;
          default: ans = Double.NaN;
        }

        writeJson(ans : ans)
      }

      }.start(54321)
  }//main
}
