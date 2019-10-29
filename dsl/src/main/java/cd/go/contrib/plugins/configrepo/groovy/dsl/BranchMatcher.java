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

import cd.go.contrib.plugins.configrepo.groovy.dsl.util.BranchFetcher;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Pattern;

import static groovy.lang.Closure.DELEGATE_ONLY;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
public class BranchMatcher extends Node<GitMaterial> {

    @NotEmpty
    private String url;

    @NotNull
    private Pattern pattern;

    @Getter(value = NONE)
    @Setter(value = NONE)
    @NotNull
    private Pipelines pipelines;

    public BranchMatcher(@NotNull Pipelines pipelines) {
        this.pipelines = pipelines;
    }

    public void onMatch(@DelegatesTo(value = Pipelines.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.BranchContext") Closure cl) {
        List<String> branches = BranchFetcher.branchesMatching(url, pattern);
        final Closure lambda = cl.dehydrate();
        lambda.setDelegate(pipelines);
        lambda.setResolveStrategy(DELEGATE_ONLY);
        branches.forEach((String branch) -> {
            BranchContext ctx = new BranchContext(url, branch);
            lambda.call(ctx);
        });
    }
}
