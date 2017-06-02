package org.mapcreator

import java.io.Serializable
import org.kohsuke.github.GitHub

class Changes implements Serializable {
    protected String username
    protected String key
    protected String repo
    
    def init(username, key, repo) {
        this.username = username
        this.key = key
        this.repo = repo
    }
    
    @NonCPS
    def getByCommit(sha) {
        def git = GitHub.connect(this.username, this.key)
        def repo = git.getRepository(this.repo)

        def changes = repo.getCommit(sha)

        def files = []

        for(item in changes.getFiles()) {
            file = new GitFile()
            file.init(item.getFileName(), item.getStatus())

            files += file
        }

        return files
    }
}