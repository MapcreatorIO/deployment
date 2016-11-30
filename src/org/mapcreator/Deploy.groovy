package org.mapcreator

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils
import java.io.Serializable

class Deploy implements Serializable {
	String deployPath
	String deployBase
	String buildNumber
	
    def steps
    String key
    String host
    String user
	boolean debug    
    def shell
    def utils

  	Deploy(steps, String key, String host, String user, boolean debug) {
  		this.steps = steps
  		this.key = key
  		this.host = host
  		this.user = user
  		this.debug = debug

		this.utils = new ShUtils(steps)
		this.shell = new SecureShell(steps)
		this.shell.init(key, host, user, debug)  		
  	}

	def init(String base, String projectName, String deployName, String buildNumber) {
		this.buildNumber = buildNumber

		if(base[-1] != '/') {
			base += '/'
		}

		this.deployBase = sprintf('%s%s/%s/', [base, projectName, deployName])

		this.deployPath = sprintf('%srevisions/jenkins-%s-%s-%s', [this.deployBase, utils.getUnixEpoch(), this.buildNumber, utils.getRevision()])
		this.debug = debug
	}

	def unStash() {
		steps.echo 'Unstashing'

		steps.step([$class: 'WsCleanup'])
		steps.unstash 'deployable'
	}

	def prepare(List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		commands += sprintf('mkdir -pv %s %s/shared', [this.deployPath, this.deployBase])
		commands += sprintf('cd %s', [this.deployPath])

		commands += append

		this.shell.ssh(commands)
	}

	def copy() {
		steps.echo 'Copying files...'

		this.shell.scp('./*', deployPath + '/')
	}	

	def finish(List shared = [], List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		for(item in shared) {
            commands += sprintf('rm -rv %s/%s || true', [this.deployPath, item])
			commands += sprintf('ln -sv %s/shared/%s %s/%s', [this.deployBase, item, this.deployPath, item])			
		}

		commands += sprintf('rm -v %s/current', [this.deployBase])
 		commands += sprintf('ln -svf %s %s/current', [this.deployPath, this.deployBase])		
		
		commands += append

		this.shell.ssh(commands)
	}
}
