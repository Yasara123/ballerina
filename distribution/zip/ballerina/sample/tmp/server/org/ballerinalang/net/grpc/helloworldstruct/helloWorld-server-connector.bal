package server.org.ballerinalang.net.grpc.helloworldstruct;

import ballerina.io;
import ballerina.net.grpc;

@grpc:serviceConfig {port:9090}
service<grpc> helloWorld {
    resource hello (grpc:ServerConnection conn, HelloRequest req) {
        io:println("Receive hello from : " + name);
        HelloResponse res = {};
        res.message = "Hello " + req.name; // response message
        grpc:ConnectorError err = conn.send(res);
        if (err != null) {
            io:println("Error at helloWorld : " + err.message);
        }
        _ = conn.complete();
    }
}

struct HelloRequest {
    string name;
}

struct HelloResponse {
    string message;
}
