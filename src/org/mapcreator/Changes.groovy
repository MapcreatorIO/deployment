package org.mapcreator

import java.io.Serializable
import org.kohsuke.github.GitHub

class Changes implements Serializable {
    protected String username
    protected String key
    protected String repo
    protected boolean debug
    
    def init(username, key, repo, debug = false) {
        this.username = username
        this.key = key
        this.repo = repo
        this.debug = debug
    }

    def log(String message) {
        if(debug) {
            println(message)
        }
    }
    
    @NonCPS
    def getByCommit(sha) {
        this.log("Connecting to github...")

        def git = GitHub.connect(this.username, this.key)
        def repo = git.getRepository(this.repo)

        this.log("Getting files from ${sha}")
        def changes = repo.getCommit(sha)

        def files = []

        for(item in changes.getFiles()) {
            file = new GitFile(name: item.getFileName(), status: item.getStatus())
            this.log("Adding item ${file.name}")
            files += file
        }

        this.log("Returning items...")
        return files
    }
}