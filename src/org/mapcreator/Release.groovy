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
		git = GitHub.connect(this.username, this.key)
		repo = git.getRepository(repo)
		release = repo.listReleases()[0]

		return release.getTarballUrl()
	}
}
