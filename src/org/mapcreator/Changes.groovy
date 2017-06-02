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
        
        def files = []

        try {
            def changes = repo.getCommit(sha)

            for(file in changes.getFiles()) {
                files += file.getFileName()
            }
        } finally {
            return files
        }
    }
}