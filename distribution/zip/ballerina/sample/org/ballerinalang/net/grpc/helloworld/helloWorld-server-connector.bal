package org.ballerinalang.net.grpc.helloworld;

import ballerina.net.grpc;
import ballerina.io;

@grpc:serviceConfig{port:9090}
service<grpc> helloWorld {
    resource hello (grpc:ServerConnection conn, string name) {
	io:print("name: " +name);
        string message = "Hello " + name; // response message
        grpc:ConnectorError err = conn.send(message);
        if (err != null) {
          // code to execute when there connector error
        }
	_ = conn.complete();
    }
}
