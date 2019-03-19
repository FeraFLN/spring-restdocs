/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.restdocs.operation;

import java.net.URI;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Tests for {@link QueryStringParser}.
 *
 * @author Andy Wilkinson
 */
public class QueryStringParserTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final QueryStringParser queryStringParser = new QueryStringParser();

	@Test
	public void noParameters() {
		Parameters parameters = this.queryStringParser
				.parse(URI.create("http://localhost"));
		assertThat(parameters.size()).isEqualTo(0);
	}

	@Test
	public void singleParameter() {
		Parameters parameters = this.queryStringParser
				.parse(URI.create("http://localhost?a=alpha"));
		assertThat(parameters.size()).isEqualTo(1);
		assertThat(parameters).containsEntry("a", Arrays.asList("alpha"));
	}

	@Test
	public void multipleParameters() {
		Parameters parameters = this.queryStringParser
				.parse(URI.create("http://localhost?a=alpha&b=bravo&c=charlie"));
		assertThat(parameters.size()).isEqualTo(3);
		assertThat(parameters).containsEntry("a", Arrays.asList("alpha"));
		assertThat(parameters).containsEntry("b", Arrays.asList("bravo"));
		assertThat(parameters).containsEntry("c", Arrays.asList("charlie"));
	}

	@Test
	public void multipleParametersWithSameKey() {
		Parameters parameters = this.queryStringParser
				.parse(URI.create("http://localhost?a=apple&a=avocado"));
		assertThat(parameters.size()).isEqualTo(1);
		assertThat(parameters).containsEntry("a", Arrays.asList("apple", "avocado"));
	}

	@Test
	public void encoded() {
		Parameters parameters = this.queryStringParser
				.parse(URI.create("http://localhost?a=al%26%3Dpha"));
		assertThat(parameters.size()).isEqualTo(1);
		assertThat(parameters).containsEntry("a", Arrays.asList("al&=pha"));
	}

	@Test
	public void malformedParameter() {
		this.thrown.expect(IllegalArgumentException.class);
		this.thrown
				.expectMessage(equalTo("The parameter 'a=apple=avocado' is malformed"));
		this.queryStringParser.parse(URI.create("http://localhost?a=apple=avocado"));
	}

}
