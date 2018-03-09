package server.org.ballerinalang.net.grpc.serverstream;

import ballerina.net.grpc;
import ballerina.io;

@grpc:serviceConfig{port:9090}
service<grpc> helloWorld {

    @grpc:resourceConfig{streaming:true}
    resource lotsOfReplies(grpc:ServerConnection conn, string name) {
      string[] greets = ["WSO2", "IBM", "Apache"];
      foreach greet in greets {
         grpc:ConnectorError err = conn.send(greet + " " + name);
         if (err != null) {
             io:println("Error at lotsOfReplies : " + err.message);
         }
      }
      // Once all messages are sent, server send complete message to notify the client, I’m done.
      _ = conn.complete();
    }
}
