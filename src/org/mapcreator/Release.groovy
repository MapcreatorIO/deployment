package org.mapcreator

import java.io.Serializable
import org.kohsuke.github.GitHub

class Release implements Serializable {
	protected String username
	protected String key

	def init(username, key) {
		this.username = username
		this.key = key
	}	

	@NonCPS
	def getLatest(repo) {
		def git = GitHub.connect(this.username, this.key)
		def repository = git.getRepository(repo)
		def release = repository.listReleases()[0]

		return release.getTarballUrl()
	}
	
	@NonCPS
	def getLatestBody(repo) {
		def git = GitHub.connect(this.username, this.key)
		def repository = git.getRepository(repo)
		def release = repository.listReleases()[0]
		
		return release.getBody();
	}
}
