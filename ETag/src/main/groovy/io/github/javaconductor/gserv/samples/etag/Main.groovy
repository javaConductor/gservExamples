
package io.github.javaconductor.gserv.samples.etag

import io.github.javaconductor.gserv.*
import io.github.javaconductor.gserv.plugins.*
import io.github.javaconductor.gserv.utils.Encoder

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
      strongETag( "/*"){exch, data ->
        //MD5 it
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

//    https://github.com/javaConductor/gserv/issues/11
//      THIS DOES NOT WORK - ONLY Context is passed.
//      // This will create an eTag based sole on the request values
//      // this is fine as long as a set of results always returns the same value.
//      /// Example: when a GET is sent tot this resource like: /add/23.12/43.45, the
//      /// response will always be 66.57
//      weakETag( "/answers/:op/:num1/:num2"){op, num1, num2 ->
//        Encoder.md5WithBase64("$num1_$op_$num2".bytes)
//      }

/*      weakETag( "/answers/:op/:num1/:num2"){ context ->
        Encoder.md5WithBase64(context.requestURI.path.bytes)
      }
*/
      }.start(54321)
  }//main
}
