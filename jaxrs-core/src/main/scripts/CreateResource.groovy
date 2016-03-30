/*
 * Copyright 2016 the original author or authors.
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

description("Creates a new JAX-Rs resource") {
    usage "grails create-resource [RESOURCE NAME]"
    argument name: 'Resource Name', description: "The name of the resource"
}

model = model(args[0])

def data = [
    packagePath : model.packagePath,
    packageName : model.packageName,
    resourceName: model.simpleName
]
if (data.resourceName.endsWith('Resource')) {
    model.resourceName -= 'Resource'
}
data.resourcePath = data.resourceName.toLowerCase()

render template: "artifacts/Resource.groovy",
    destination: file("grails-app/resources/$data.packagePath/${data.resourceName}Resource.groovy"),
    model: data
