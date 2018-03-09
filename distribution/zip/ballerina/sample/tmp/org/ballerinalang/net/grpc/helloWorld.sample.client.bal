package org.ballerinalang.net.grpc;
import ballerina.io;

function main (string[] args) {
    
    endpoint<helloWorldBlockingStub> helloWorldStubBlocking {
        create helloWorldBlockingStub("localhost", 9090);
    }

   var res,ee = helloWorldStubBlocking.hello("WSO2");
    if (ee != null) {
        io:println("Error from Connector: " + ee.message);        
    } else {
    	io:println("Client Got Response : ");
    	io:println(res);
   }

}
