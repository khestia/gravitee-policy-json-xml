/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.policy.v3.json2xml;

import static io.vertx.core.http.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

import io.gravitee.apim.gateway.tests.sdk.annotations.DeployApi;
import io.gravitee.apim.gateway.tests.sdk.configuration.GatewayConfigurationBuilder;
import io.gravitee.definition.model.Api;
import io.gravitee.definition.model.ExecutionMode;
import io.gravitee.policy.json2xml.JsonToXmlTransformationPolicyIntegrationTest;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.http.HttpClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class JsonToXmlTransformationPolicyV3IntegrationTest extends JsonToXmlTransformationPolicyIntegrationTest {

    @Override
    protected void configureGateway(GatewayConfigurationBuilder gatewayConfigurationBuilder) {
        super.configureGateway(gatewayConfigurationBuilder);
        gatewayConfigurationBuilder.set("api.jupiterMode.enabled", "false");
    }

    @Override
    public void configureApi(Api api) {
        super.configureApi(api);
        api.setExecutionMode(ExecutionMode.V3);
    }

    @Test
    @DisplayName("Should return Bad Request when posting invalid json to gateway")
    @DeployApi("/apis/api-pre.json")
    void shouldReturnBadRequestWhenPostingInvalidJsonToGateway(HttpClient client) throws InterruptedException {
        final String input = loadResource("/io/gravitee/policy/json2xml/invalid-input.json");

        client
            .rxRequest(POST, "/test")
            .flatMap(request -> request.rxSend(Buffer.buffer(input)))
            .flatMapPublisher(
                response -> {
                    assertThat(response.statusCode()).isEqualTo(500);
                    return response.toFlowable();
                }
            )
            .test()
            .await()
            .assertComplete()
            .assertNoErrors();
    }
}