package server.org.ballerinalang.net.grpc.clientstream;

import ballerina.io;
import ballerina.net.grpc;

@grpc:serviceConfig {rpcEndpoint:"LotsOfGreetings",
                     clientStreaming:true}
service<grpc> helloWorld {
    resource onOpen (grpc:ServerConnection conn) {
        io:println("Connnection has established sucessfully.");
    }

    resource onMessage (grpc:ServerConnection conn, string name) {
        io:println("onMessage: " + name);
        if (name == "WSO2") {
            string message = "Hello " + name;
            grpc:ConnectorError err = conn.send(message);
            if (err != null) {
                io:println("Error at onMessage send message : " + err.message);
            }
        } else {
            grpc:ServerError serverErr = {};
            serverErr.msg = "Illegal input message";
            serverErr.statusCode = 3;
            grpc:ConnectorError err = conn.error(serverErr);
            if (err != null) {
                io:println("Error at onMessage send error : " + err.message);
            }
        }
    }

    resource onError (grpc:ServerConnection conn, grpc:ServerError err) {
        if (err != null) {
            io:println("Something unexpected happens at server : " + err.message);
        }
    }

    resource onComplete (grpc:ServerConnection conn) {
        io:println("Server Response");
        string message = "Hello Everyone";
        grpc:ConnectorError err = conn.send(message);
        if (err != null) {
            io:println("Error at onComplete send message : " + err.message);
        }
    }
}

