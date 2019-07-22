package org.mapcreator;

import java.io.Serializable

class ShUtils implements Serializable {
    def steps
  	ShUtils(steps) {this.steps = steps}	

	def rm(String path) {
		steps.sh "rm -vr ${path} || true"
	}

	def mkdir(String path) {
		steps.sh "mkdir -pv ${path}"
	}

	def getUnixEpoch() {
		return steps.sh(
			script: 'date +%s',
			returnStdout: true
		).trim()
	}

	def isManualBuild() {
		return current.build.rawBuild.getCause(hudson.model.Case$UserIdCase) != null
	}
	
	def getLastTag() {
	    return steps.sh(
	        returnStdout: true,
	        script: "git tag --sort version:refname | tail -1"
	    ).trim()
	}

	def getRevision() {
    	return steps.sh(
        	returnStdout: true,
        	script: 'git rev-parse HEAD'
    	).trim().take(6)
	}
}
