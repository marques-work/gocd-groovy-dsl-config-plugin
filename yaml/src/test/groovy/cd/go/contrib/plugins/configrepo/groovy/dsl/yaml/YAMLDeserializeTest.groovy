/*
 * Copyright 2018 ThoughtWorks, Inc.
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
package cd.go.contrib.plugins.configrepo.groovy.dsl.yaml

import cd.go.contrib.plugins.configrepo.groovy.dsl.Node
import cd.go.contrib.plugins.configrepo.groovy.dsl.TestBase
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import javax.validation.ConstraintViolation
import java.util.function.Consumer

import static org.assertj.core.api.Assertions.assertThat

class YAMLDeserializeTest extends TestBase {
  GoCDYamlSerializer serializer = new GoCDYamlSerializer()

  @ParameterizedTest
  @MethodSource("values")
  void testTransformRoundYAML(String path) {
    def engine = getRunner()
    def result = engine.runScript(path + '.groovy')
    Set<ConstraintViolation<Object>> constraintViolations = null
    def consumer = new Consumer<Set<ConstraintViolation<Object>>>() {

      @Override
      void accept(Set<ConstraintViolation<Object>> errors) {
        constraintViolations = errors
      }
    }
    validate(result, consumer)

    assertThat((Set) constraintViolations).isNullOrEmpty()
    assertThat(result).isInstanceOf(Node)

    def actualYaml = serializer.toYAMLString(result as Node)
    def object = serializer.fromYaml(actualYaml, result.getClass())
    assertThat(object).isExactlyInstanceOf(result.class)
    assertThat(object).isEqualTo(result)
  }

}
