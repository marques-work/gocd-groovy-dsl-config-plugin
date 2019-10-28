/*
 * Copyright 2019 ThoughtWorks, Inc.
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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BranchContext {

    @NotEmpty
    private String branch;

    @NotEmpty
    private String branchSanitized;

    @NotNull
    private GitMaterial repo;

    public BranchContext(String url, String branch) {
        this.branch = branch;
        branchSanitized = branch.replaceAll("[^\\w\\-]", "_");

        repo = new GitMaterial();
        repo.name = "main";
        repo.setUrl(url);
        repo.setBranch(branch);
        repo.setShallowClone(true);
    }
}
