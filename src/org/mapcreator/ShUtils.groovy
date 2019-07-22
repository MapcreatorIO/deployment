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
        	script: 'if [ $(git status 2&> /dev/null 1>&2; echo $?) -eq 0 ]; then git rev-parse HEAD | cut -c1-6 | tail -1; else svn info . | sed -n -e "s/^Revision: //p"; fi'
    	).trim()
	}
}
