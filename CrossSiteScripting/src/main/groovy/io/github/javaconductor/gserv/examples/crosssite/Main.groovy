package io.github.javaconductor.gserv.examples.crosssite

import io.github.javaconductor.gserv.GServ

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
    ]

    gserv.plugins{
      plugin ( 'cors', [:] )
    }.http{
      useResourceDocs(true)
      get('/index.html',file("text/html", "crosssite/index.html"))

    }.start(60000);

    gserv.plugins{
      plugin ( 'cors', [:] )
    }.http{
      cors('/public', allowAll(3600))
      cors('/private/*', whiteList(3600, hostList))
      cors('/internal', whiteList(3600, hostListWithLocalhost))

      useResourceDocs(true)

      get('/public'){ ->
        write( "This is the public message.")
      }

      get('/private'){ ->
        write( "This is the private message.")
      }

      get('/internal'){ ->
        write( "This is the internal message.")
      }

    }.start(60001);
  };

}
