package org.mapcreator;

import org.mapcreator.ShellUtils;

class Deploy {
	private String deployPath
	private String deployBase
	private String key
	private String host
	private String user
	private boolean debug

	def init(String key, String host, String user, String base, String projectName, String deployName) {
		if(base[-1] != '/') {
			base += '/'
		}


		this.deployBase = sprintf('%s%s/%s/', [base, projectName, deployName])
		this.deployPath = sprintf('%srevisions/jenkins-%s-${BUILD_NUMBER}-%s', [this.deployBase, ShellUtils.getUnixEpoch(), ShellUtils.getRevision()])
		this.debug = false
	}

	def enableDebug() {
		this.debug = true
	}

	def unStash() {
		echo 'Unstashing'

		step([$class: 'wsCleanup'])
		unstash 'deployable'
	}

	def prepare(List prepend = [], List append = []) {
		commands = []
		commands += prepend

		commands += sprintf('mkdir -pv %s %s/shared', [deployPath, deployBasePath])
		commands += sprintf('cd %s', [deployPath])

		commands += append

		if(!this.debug) {
			ShellUtils.ssh(this.user, this.host, this.key, commands)
		} else {
			println 'Debug SSH: \n'
			println 'User: ' + this.user
			println 'Host: ' + this.host
			println 'Key: ' + this.key
			println 'Commands: \n' + commands.join('\n')
		}
	}

	def copy() {
		echo 'Copying files...'

		if(!this.debug) {
			ShellUtils.scp(this.user, this.host, this.key, './*', deployPath + '/')			
		} else {
			println 'Debug SCP: \n'
			println 'User: ' + this.user
			println 'Host: ' + this.host
			println 'Key: ' + this.key
			println 'From: ' + './*'
			println 'To: ' + deployPath + '/'
		}
	}	

	def finish(List shared = [], List prepend = [], List append = []) {
		finish = []
		finish += prepend

		for(item in shared) {
            finish += sprintf('rm -rv %s/%s || true', [this.deployPath, item])
			finish += sprintf('ln -sv %s/shared/%s %s/%s', [this.deployBase, item, this.deployPath, item])			
		}

		finish += sprintf('rm -v %s/current', [this.deployBase])
 		finish += sprintf('ln -svf %s %s/current', [this.deployPath, this.deployBase])		
		
		finish += append

		if(!this.debug) {
			ShellUtils.ssh(this.user, this.host, this.key, commands)
		} else {
			println 'Debug SSH: \n'
			println 'User: ' + this.user
			println 'Host: ' + this.host
			println 'Key: ' + this.key
			println 'Commands: \n' + commands.join('\n')
		}
	}
}