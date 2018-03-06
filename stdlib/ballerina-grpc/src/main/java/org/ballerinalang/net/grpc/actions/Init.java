/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.ballerinalang.net.grpc.actions;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.ballerinalang.bre.Context;
import org.ballerinalang.connector.api.AbstractNativeAction;
import org.ballerinalang.connector.api.BallerinaConnectorException;
import org.ballerinalang.connector.api.ConnectorFuture;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BConnector;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BStruct;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.nativeimpl.actions.ClientConnectorFuture;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaAction;
import org.ballerinalang.net.grpc.ClientConnectorFactory;
import org.ballerinalang.net.grpc.ClientSSLConfigs;
import org.ballerinalang.net.grpc.exception.GrpcClientException;
import org.ballerinalang.net.grpc.stubs.GrpcBlockingStub;
import org.ballerinalang.net.grpc.stubs.GrpcNonBlockingStub;
import org.ballerinalang.net.grpc.stubs.ProtoFileDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLException;

/**
 * {@code Init} is the Init action implementation of the gRPC Connector.
 */
@BallerinaAction(
        packageName = "ballerina.net.grpc",
        actionName = "<init>",
        connectorName = "GRPCConnector",
        args = {@Argument(name = "c", type = TypeKind.CONNECTOR)},
        connectorArgs = {
                @Argument(name = "host", type = TypeKind.STRING),
                @Argument(name = "port", type = TypeKind.INT),
                @Argument(name = "type", type = TypeKind.STRING),
                @Argument(name = "descriptorKey", type = TypeKind.STRING),
                @Argument(name = "options", type = TypeKind.STRUCT, structType = "Options",
                        structPackage = "ballerina.net.grpc"),
        })
public class Init extends AbstractNativeAction {
    
    @Override
    public ConnectorFuture execute(Context context) {
        BConnector bConnector = (BConnector) getRefArgument(context, 0);
        int port = (int) bConnector.getIntField(0);
        String host = bConnector.getStringField(0);
        String subtype = bConnector.getStringField(1);
        //EnumNode.Enumerator enumerator = (EnumNode.Enumerator) bConnector.getRefField(1);
        String descriptorKey = bConnector.getStringField(2);
        BMap<String, BValue> descriptorMap = (BMap<String, BValue>) bConnector.getRefField(0);
        ClientConnectorFuture future = new ClientConnectorFuture();
        
        try {
            // If there are more than one descriptors exist, other descriptors are considered as dependent
            // descriptors.  client supported only one depth descriptor dependency.
            List<byte[]> depDescriptorData = new ArrayList<>();
            byte[] descriptorValue = null;
            for (String key : descriptorMap.keySet()) {
                if (descriptorMap.get(key) == null) {
                    continue;
                }
                if (descriptorKey.equals(key)) {
                    descriptorValue = hexStringToByteArray(descriptorMap.get(key).stringValue());
                } else {
                    depDescriptorData.add(hexStringToByteArray(descriptorMap.get(key).stringValue()));
                }
            }
            
            if (descriptorValue == null) {
                throw new RuntimeException("Error while establishing the connection. service descriptor is null.");
            }
            ProtoFileDefinition protoFileDefinition = new ProtoFileDefinition(depDescriptorData);
            protoFileDefinition.setRootDescriptorData(descriptorValue);
            BStruct options = (BStruct) bConnector.getRefField(0);
            ClientSSLConfigs clientSslConfigs;
            ManagedChannel channel;
            if (options != null) {
                clientSslConfigs = populateClientConfigurationOptions(options);
                File certFile = new File(clientSslConfigs.getTrustStoreFile());
                SslContext sslContext = SslContextBuilder.forClient().trustManager(certFile).build();
                channel = NettyChannelBuilder.forAddress(host, port)
                        .sslContext(sslContext)
                        .usePlaintext(true)
                        .build();
            } else {
                channel = ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext(true)
                        .build();
            }
            ClientConnectorFactory clientConnectorFactory = new ClientConnectorFactory(protoFileDefinition);
            
            if ("blocking".equalsIgnoreCase(subtype)) {
                GrpcBlockingStub grpcBlockingStub = clientConnectorFactory.newBlockingStub(channel);
                bConnector.setNativeData("stub", grpcBlockingStub);
                future.notifySuccess();
            } else if ("non-blocking".equalsIgnoreCase(subtype)) {
                GrpcNonBlockingStub nonBlockingStub = clientConnectorFactory.newNonBlockingStub(channel);
                bConnector.setNativeData("stub", nonBlockingStub);
                future.notifySuccess();
            } else {
                future.notifyFailure(new BallerinaConnectorException("gRPC Connector Error : Invalid stub type"));
            }
            return future;
        } catch (RuntimeException | GrpcClientException e) {
            future.notifyFailure(new BallerinaConnectorException("gRPC Connector Error.", e));
            return future;
        } catch (SSLException e) {
            future.notifyFailure(new BallerinaConnectorException("gRPC Connector Error when generating SSL " +
                    "configuration.", e));
            return future;
        }
    }
    
    // TODO: 3/6/18 improve to support proxy
    private ClientSSLConfigs populateClientConfigurationOptions(BStruct options) {
        if (options.getRefField(0) != null) {
            ClientSSLConfigs clientSslConfigs = new ClientSSLConfigs();
            BStruct sslConfigStructs = (BStruct) options.getRefField(0);
            clientSslConfigs.setTrustStoreFile(sslConfigStructs.getStringField(0));
            return clientSslConfigs;
        }
        return new ClientSSLConfigs();
    }
    
    /**
     * Human readable to byte convection.
     *
     * @param s human readable byte string array
     * @return byte array.
     */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
