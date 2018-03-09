package server.org.ballerinalang.net.grpc.helloworld;

import ballerina.io;
import ballerina.net.grpc;

@grpc:serviceConfig {port:9090}
service<grpc> helloWorld {
    resource hello (grpc:ServerConnection conn, string name) {
        io:println("Receive hello from : " + name);
        string message = "Hello " + name; // response message
        grpc:ConnectorError err = conn.send(message);
        if (err != null) {
            io:println("Error at helloWorld : " + err.message);
        }
        _ = conn.complete();
    }
}
