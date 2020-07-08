/*
 * Copyright 2020 ThoughtWorks, Inc.
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

import cd.go.contrib.plugins.configrepo.groovy.dsl.mixins.KeyVal;
import cd.go.contrib.plugins.configrepo.groovy.dsl.util.RefUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BranchContext implements KeyVal.Mixin {

    public BranchContext() {
    }

    public BranchContext(@NotEmpty String fullRefName, @NotEmpty String branch, @NotNull ScmMaterial repo) {
        this.fullRefName = fullRefName;
        this.branch = branch;
        this.branchSanitized = RefUtils.sanitizeRef(branch);
        this.repo = repo;
    }

    @JsonProperty("full_ref_name")
    @NotEmpty
    private String fullRefName;

    @JsonProperty("branch_name")
    @NotEmpty
    private String branch;

    @JsonProperty("sanitized_branch_name")
    @NotEmpty
    private String branchSanitized;

    @JsonProperty("repo")
    @NotNull
    private ScmMaterial repo;
}