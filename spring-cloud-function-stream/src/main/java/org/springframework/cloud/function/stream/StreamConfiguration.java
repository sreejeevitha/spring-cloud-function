/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.function.stream;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.function.invoker.AbstractFunctionInvoker;
import org.springframework.cloud.function.registry.FunctionCatalog;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;

/**
 * @author Mark Fisher
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(FunctionConfigurationProperties.class)
@ConditionalOnClass({ Binder.class, AbstractFunctionInvoker.class })
@ConditionalOnProperty(name = "spring.cloud.stream.enabled", havingValue = "true", matchIfMissing = true)
public class StreamConfiguration {

	@Autowired
	private FunctionConfigurationProperties properties;

	@Bean
	@ConditionalOnProperty("spring.cloud.stream.bindings.input.destination")
	public AbstractFunctionInvoker<?, ?> invoker(FunctionCatalog registry) {
		String name = properties.getName();
		Function<Flux<Object>, Flux<Object>> function = registry.lookupFunction(name);
		return new StreamListeningFunctionInvoker(function);
	}

}