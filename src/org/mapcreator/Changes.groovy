package org.mapcreator

import java.io.Serializable
import org.kohsuke.github.GitHub

class Changes implements Serializable {
    public String username
    public String key
    public String repo

    def log(String message) {
        if(debug) {
            println(message)
        }
    }

    @NonCPS
    def getByCommit(sha) {
        def git = GitHub.connect(this.username, this.key)
        def repo = git.getRepository(this.repo)
        def changes = repo.getCommit(sha)

        def files = []

        for(item in changes.getFiles()) {
            GitFile file = new GitFile(name: item.getFileName(), status: item.getStatus())
            files += file
        }

        return files
    }
}