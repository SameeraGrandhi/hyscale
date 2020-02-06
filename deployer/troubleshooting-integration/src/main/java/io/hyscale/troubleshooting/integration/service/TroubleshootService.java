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
package io.hyscale.troubleshooting.integration.service;

import io.hyscale.commons.exception.HyscaleException;
import io.hyscale.commons.models.K8sAuthorisation;
import io.hyscale.troubleshooting.integration.models.DiagnosisReport;
import io.hyscale.troubleshooting.integration.models.ServiceInfo;

import java.util.List;

public interface TroubleshootService {

    public List<DiagnosisReport> troubleshoot(ServiceInfo serviceInfo, K8sAuthorisation k8sAuthorisation, String namespace) throws HyscaleException;
}