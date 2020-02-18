/**
 * Copyright 2019 Pramati Prism, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hyscale.deployer.services.handler.impl;


import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeAddress;
import io.kubernetes.client.models.V1NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class V1NodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(V1NodeUtil.class);
    private static final String ExternalIP = "ExternalIP";
    private static final String InternalIP = "InternalIP";


    public static List<String> getNodeIPList(ApiClient apiClient) {
        CoreV1Api coreV1Api = new CoreV1Api(apiClient);
        try {
            V1NodeList nodeList = coreV1Api.listNode("true", null, null, null, null, null, null, null);
            if (nodeList != null && nodeList.getItems() != null && !nodeList.getItems().isEmpty()) {
                List<String> ipList = new ArrayList<>();
                for (V1Node node : nodeList.getItems()) {
                    Map<String, String> typeVsAddress = new HashMap<>();
                    for (V1NodeAddress nodeAddress : node.getStatus().getAddresses()) {
                        typeVsAddress.put(nodeAddress.getType(), nodeAddress.getAddress());
                    }
                    if (typeVsAddress.getOrDefault(ExternalIP, null) != null) {
                        ipList.add(typeVsAddress.get(ExternalIP));
                        continue;
                    } else if (typeVsAddress.getOrDefault(InternalIP, null) != null) {
                        ipList.add(typeVsAddress.get(InternalIP));
                        continue;
                    }
                }
                return ipList;
            }
        } catch (ApiException e) {
            logger.error("Error while fetching node ip");
        }
        return null;
    }
}
