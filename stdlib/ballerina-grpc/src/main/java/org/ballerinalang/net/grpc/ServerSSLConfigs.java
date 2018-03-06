/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.ballerinalang.net.grpc;

public class ServerSSLConfigs {
    private String keyFile;
    private String keyCertChainFile;
    private String keyPassword;
    
    public ServerSSLConfigs(String keyFile, String keyCertChainFile) {
        this.keyFile = keyFile;
        this.keyCertChainFile = keyCertChainFile;
    }
    
    public ServerSSLConfigs() {
    }
    
    public String getKeyFile() {
        return keyFile;
    }
    
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public String getKeyCertChainFile() {
        return keyCertChainFile;
    }
    
    public void setKeyCertChainFile(String keyCertChainFile) {
        this.keyCertChainFile = keyCertChainFile;
    }
    
    public String getKeyPassword() {
        return keyPassword;
    }
    
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
}
