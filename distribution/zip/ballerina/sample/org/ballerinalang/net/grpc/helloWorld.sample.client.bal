package org.ballerinalang.net.grpc;

function main (string[] args) {
    
    endpoint<helloWorldBlockingStub> helloWorldStubBlocking {
        create helloWorldBlockingStub("localhost", 8080);
    }

    endpoint<helloWorldNonBlockingStub> helloWorldStubNonBlocking {
        create helloWorldNonBlockingStub("localhost", 8080);
    }

}
