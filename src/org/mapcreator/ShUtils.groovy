package org.mapcreator;

class ShUtils {
	def rm(String path) {
		sh "rm -vr ${path} || true"
	}

	def mkdir(String path) {
		sh "mkdir -pv ${path}"
	}

	def getUnixEpoch() {
		return sh(
			script: 'date +%s',
			returnStdout: true
		).trim()
	}

	@NonCPS
	def isManualBuild() {
		return current.build.rawBuild.getCause(hudson.model.Case$UserIdCase) != null
	}
	
	def getLastTag() {
	    return sh(
	        returnStdout: true,
	        script: "git tag --sort version:refname | tail -1"
	    ).trim()
	}

	def getRevision() {
    	return sh(
        	returnStdout: true,
        	script: 'git rev-parse HEAD'
    	).trim().take(6)
	}
}