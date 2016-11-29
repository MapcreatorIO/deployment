def rm(String f) {
    sh "rm -vr ${f} || true"
}

def mkdir(String d) {
    sh "mkdir -pv ${d}"
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

def getUnixEpoch() {
    return sh(
        returnStdout: true,
        script: 'date +%s'
    ).trim()
}

@NonCPS
def isManualBuild() {
    return currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null
}

def ssh(String user, String host, String key, List commands) {
	commandString = commands.join(';')

	echo sprintf('Running the following commands on host %s@%s\n%s', [user, host, commands.join('\n')])

	sshagent([key]) {
		return sh(
        		script: "ssh -o StrictHostKeyChecking=no ${user}@${host} << EOF\n${commandString}\nEOF",
	            	returnStdout: true
        	)
	}
}

def scp(String user, String host, String key, String from, String to) {
    echo sprintf("Copying %s to %s on host %s@%s", [from, to, user, host])

    sshagent(['f206c873-8c0b-481e-9c72-1ecb97a5213a']) {
        return sh(
            script: sprintf("scp -o StrictHostKeyChecking=no -4CBr %s %s@%s:%s/", [from, user, host, to]),
            returnStdout: true
        )
    }
}
