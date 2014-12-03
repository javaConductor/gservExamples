package io.github.javaconductor.gserv.examples.crosssite

import io.github.javaconductor.gserv.GServ
import io.github.javaconductor.gserv.plugins.cors.CORSMode

class Main {

  static void main(String[] args){

    def gserv = new GServ();
    def hostList = [
        "129.25.192.33": [
          methods             : ["GET", "PUT", "POST"],
          maxAge              : 7200,
          customRequestHeaders: ['X-Custom-Header']
        ]
    ];

    def hostListWithLocalhost = [
        "localhost": [
          methods             : ["GET", "PUT", "POST"],
          maxAge              : 7200,
          customRequestHeaders: ['X-Custom-Header']
        ]
    ];

    gserv.plugins{
      plugin ( 'cors', [:] )
    }.http{
      cors('/public', allowAll(3600))
      cors('/private', whiteList(3600, hostList))
      cors('/internal', whiteList(3600, hostListWithLocalhost))

      useResourceDocs(true)

      get('/index.html',file("text/html", "crosssite/index.html"))
      get('/public'){ ->
        writeJson(msg: "This is the public message.")
      }

      get('/private'){ ->
        writeJson(msg: "This is the private message.")
      }

      get('/internal'){ ->
        writeJson(msg: "This is the internal message.")
      }

    }.start(60000);
  };

}
