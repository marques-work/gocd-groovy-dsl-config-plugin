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

package cd.go.contrib.plugins.configrepo.groovy.dsl;

import cd.go.contrib.plugins.configrepo.groovy.dsl.util.CheckAtLeastOneNotEmpty;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.SimpleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

import static groovy.lang.Closure.DELEGATE_ONLY;

/**
 * Executes a shell script.
 * <p>
 * {@includeCode shell-large-examples.groovy}
 */
@Getter
@Setter
@CheckAtLeastOneNotEmpty(fieldNames = {"commandString", "file"})
@ToString
public class ShellTask extends Task<ShellTask> {

    /**
     * The shell command to be executed {@code bash}, {@code zsh}. Must be on PATH.
     */
    @NotEmpty
    private String shell;

    /**
     * The directory in which the script or command is to be executed.
     * <p>
     * Note that this directory is relative to the directory where the agent checks out the materials.
     */
    private String workingDir;

    /**
     * The command string to be executed using the {@link #shell}'s {@code -c} argument
     *
     * <p>
     * <strong>Note: </strong> Must either specify {@link #commandString} or {@link #file}
     * <p>
     * This is the same as running:
     * <p>
     * {@includeCode shell-with-command-string.groovy}
     */
    private String commandString;

    /**
     * The file to be passed to the {@link #shell}.
     *
     * <p>
     * <strong>Note: </strong> Must either specify {@link #commandString} or {@link #file}
     * <p>
     * This is the same as running {@code "bash FILE"}
     */
    private String file;

    /**
     * Sets the {@code -l} or {@code --login} argument to your shell. This will usually load up the {@code .profile}
     * (or equivalent) of the shell.
     *
     * @see #loadProfile()
     */
    private Boolean login;

    ShellTask() {
        this(null, null);
    }

    public ShellTask(String shell, @DelegatesTo(value = ShellTask.class, strategy = DELEGATE_ONLY) @ClosureParams(value = SimpleType.class, options = "cd.go.contrib.plugins.configrepo.groovy.dsl.ShellTask") Closure cl) {
        super();
        this.shell = shell;
        configure(cl);
    }



    public void loadProfile() {
        login = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null) {
            return false;
        }

        if (o.getClass() == ExecTask.class) {
            return this.toExecTask().equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }


        if (!super.equals(o)) return false;

        ShellTask shellTask = (ShellTask) o;

        if (shell != null ? !shell.equals(shellTask.shell) : shellTask.shell != null) return false;
        if (workingDir != null ? !workingDir.equals(shellTask.workingDir) : shellTask.workingDir != null) return false;
        if (commandString != null ? !commandString.equals(shellTask.commandString) : shellTask.commandString != null)
            return false;
        if (file != null ? !file.equals(shellTask.file) : shellTask.file != null) return false;
        return login != null ? login.equals(shellTask.login) : shellTask.login == null;
    }

    public ExecTask toExecTask() {
        ExecTask execTask = new ExecTask();
        execTask.setWorkingDir(getWorkingDir());

        execTask.getCommandLine().add(getShell());

        if (Boolean.TRUE.equals(getLogin())) {
            execTask.getCommandLine().add("-l");
        }

        if (!(getCommandString() == null || ((CharSequence) getCommandString()).length() == 0)) {
            execTask.getCommandLine().add("-c");
            execTask.getCommandLine().add(getCommandString());
        } else {
            execTask.getCommandLine().add(getFile());
        }

        return execTask;
    }

}
