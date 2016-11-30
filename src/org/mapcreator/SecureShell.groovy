package org.mapcreator;

import java.io.Serializable

class SecureShell implements Serializable {
	String key;
	String host;
	String user;
	boolean debug;

    def steps
  	SecureShell(steps) {this.steps = steps}	

	def init(String key, String host, String user, boolean debug = false) {
		this.key = key
		this.host = host
		this.user = user
		this.debug = debug
	}

	def ssh(List commands) {
		String commandString = ''

		for(item in commands) {
			if(item[-1] != ';') {
				item += ';'
			}

			commandString += item
		}

		steps.echo sprintf("Key: %s\nUser: %s\nHost: %s\nCommands:\n%s", [this.key, this.user, this.host, commands.join('\n')])				

		if(!debug) {
			steps.sshagent([this.key]) {
				return steps.sh(
					script:  "ssh -o StrictHostKeyChecking=no ${this.user}@${this.host} << EOF\n${commandString}\nEOF",
					returnStdout: true
				)
			}
		}
	}	

	def scp(String from, String to) {
		steps.echo sprintf("Copying %s to %s on host %s@%s with key %s", [from, to, this.user, this.host, this.key])

		if(to[-1] != '/') {
			to += '/'
		}

		if(!debug) {
			steps.sshagent([this.key]) {
				return steps.sh(
					script: sprintf("scp -o StrictHostKeyChecking=no -4CBr %s %s@%s:%s", [from, this.user, this.host, to]),
					returnStdout: true
				)
			}
		}
	}
}