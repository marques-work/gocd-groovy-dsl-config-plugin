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

package cd.go.contrib.plugins.configrepo.groovy.dsl.jsonschema;

import cd.go.contrib.plugins.configrepo.groovy.dsl.GoCD;
import cd.go.contrib.plugins.configrepo.groovy.dsl.json.GoCDJsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.customProperties.ValidationSchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema;
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(new GoCDJsonSerializer().mapper(), new ValidationSchemaFactoryWrapper());
            JsonSchema jsonSchema = schemaGenerator.generateSchema(GoCD.class);
            ObjectSchema gocdSchema = jsonSchema.asObjectSchema();
            recursivelyDisallowAdditionalProperties(gocdSchema);
            System.out.println("jsonSchema = " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(gocdSchema));
        } catch (Exception e) {
            System.out.println(" Bad!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void recursivelyDisallowAdditionalProperties(ObjectSchema someSchema) {
        if (!(someSchema.getAdditionalProperties() instanceof ObjectSchema.SchemaAdditionalProperties)) {
            someSchema.setAdditionalProperties(ObjectSchema.NoAdditionalProperties.instance);
        }
        Map<String, JsonSchema> properties = someSchema.getProperties();

        properties.forEach((s, jsonSchema) -> {
            if (jsonSchema instanceof ObjectSchema) {
                recursivelyDisallowAdditionalProperties((ObjectSchema) jsonSchema);
            }
            if (jsonSchema instanceof ArraySchema) {
                recursivelyDisallowAdditionalProperties((ArraySchema) jsonSchema);
            }
        });
    }

    private static void recursivelyDisallowAdditionalProperties(ArraySchema jsonSchema) {
        ArraySchema.Items items = jsonSchema.getItems();
        if (items instanceof ArraySchema.SingleItems) {
            JsonSchema schema = ((ArraySchema.SingleItems) items).getSchema();
            if (schema instanceof ObjectSchema) {
                recursivelyDisallowAdditionalProperties((ObjectSchema) schema);
            }
            if (schema instanceof ArraySchema) {
                recursivelyDisallowAdditionalProperties((ArraySchema) schema);
            }
        }
    }

}
