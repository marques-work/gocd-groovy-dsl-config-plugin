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

package cd.go.contrib.plugins.configrepo.groovy.dsl.util;

import org.codehaus.groovy.runtime.ProcessGroovyMethods;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class BranchFetcher {

    static void log(String msg) {
        Writer output;
        try {
            output = new BufferedWriter(new FileWriter("/tmp/plugin.log"));  //clears file every time
            output.append(msg + "\n");
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> branchesMatching(String url, Pattern filter) {
        return Collections.singletonList("FOOBAR");
    }

    public static List<String> mbranchesMatching(String url, Pattern filter) {
        Process git;
        try {
            log("FETCHING branches");
            git = ProcessGroovyMethods.execute(new String[]{"git", "ls-remote", "--refs", url});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder out = new StringBuilder();
        StringBuilder err = new StringBuilder();

        ProcessGroovyMethods.waitForProcessOutput(git, out, err);

        if (0 != git.exitValue()) {
            log("FAILED to fetch branches at " + url + " ; exit " + git.exitValue());
            throw new RuntimeException(format("`git ls-remote` failed for %s! Exit status: %d; STDERR: %s", url, git.exitValue(), err.toString()));
        }

        final List<String> branches = new ArrayList<>();
        final Predicate<String> branchFilter = filter.asPredicate();

        out.toString().lines().forEach((line) -> {
            String branch = line.replaceFirst("^[0-9a-fA-F]+\\s+", "");
            log("Considering " + branch);

            if (branchFilter.test(branch)) {
                log("Branch matches: " + branch);
                branches.add(branch.replaceFirst("^refs/([^/]+)/", ""));
            }
        });

        log("Found " + branches.size() + " branches");
        return branches;
    }
}
