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
package io.hyscale.troubleshooting.integration.conditions;

import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.deployer.core.model.ResourceKind;
import io.hyscale.troubleshooting.integration.errors.TroubleshootErrorCodes;
import io.hyscale.troubleshooting.integration.models.*;
import io.kubernetes.client.models.V1Pod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//TODO JAVADOC
@Component
public class MultipleContainerRestartsCondition extends ConditionNode<TroubleshootingContext> {

    private static final Logger logger = LoggerFactory.getLogger(MultipleContainerRestartsCondition.class);

    @Autowired
    private IsPodsReadinessFailing isPodsReadinessFailing;

    @Autowired
    private IsApplicationCrashing isApplicationCrashing;

    @Override
    public boolean decide(TroubleshootingContext context) throws HyscaleException {
        DiagnosisReport report = new DiagnosisReport();
        List<TroubleshootingContext.ResourceInfo> resourceInfos = context.getResourceInfos().get(ResourceKind.POD.getKind());
        if (resourceInfos == null || resourceInfos.isEmpty()) {
            report.setReason(AbstractedErrorMessage.SERVICE_NOT_DEPLOYED.formatReason(context.getServiceInfo().getServiceName()));
            report.setRecommendedFix(AbstractedErrorMessage.SERVICE_NOT_DEPLOYED.getMessage());
            context.addReport(report);
            throw new HyscaleException(TroubleshootErrorCodes.SERVICE_IS_NOT_DEPLOYED, context.getServiceInfo().getServiceName());
        }

        Object obj = context.getAttribute(FailedResourceKey.FAILED_POD);
        List<V1Pod> podList = null;
        if (obj == null) {
            podList = resourceInfos.stream().filter(each -> {
                return each != null && each.getResource() != null && each.getResource() instanceof V1Pod;
            }).map(resourceInfo -> {
                return (V1Pod) resourceInfo.getResource();
            }).collect(Collectors.toList());
        } else {
            V1Pod failedPod = (V1Pod) FailedResourceKey.FAILED_POD.getKlazz().cast(obj);
            podList = new LinkedList<>();
            podList.add(failedPod);
        }

        return podList.stream().anyMatch(each -> {
            if (each instanceof V1Pod) {
                V1Pod pod = (V1Pod) each;
                return pod.getStatus().getContainerStatuses().stream().anyMatch(containerStatus -> {
                    if (containerStatus.getRestartCount() > 0) {
                        // Passing the failed pod to the next nodes when it is not set in the context
                        context.addAttribute(FailedResourceKey.RESTARTS, true);
                        if (obj == null) {
                            context.addAttribute(FailedResourceKey.FAILED_POD, pod);
                        }
                        return true;
                    }
                    return false;
                });
            }
            return false;
        });
    }

    @Override
    public Node<TroubleshootingContext> onSuccess() {
        return isApplicationCrashing;
    }

    @Override
    public Node<TroubleshootingContext> onFailure() {
        return isPodsReadinessFailing;
    }


    @Override
    public String describe() {
        return "Multiple container restarts ?";
    }

}
