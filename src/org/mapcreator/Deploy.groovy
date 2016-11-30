package org.mapcreator

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils

class Deploy {
	private String deployPath
	private String deployBase
	private String host
	private String user
	private boolean debug
	private ShUtils utils
	private SecureShell shell

	def init(String key, String host, String user, String base, String projectName, String deployName, boolean debug = false) {
		if(base[-1] != '/') {
			base += '/'
		}

		this.utils = new ShUtils()

		this.deployBase = sprintf('%s%s/%s/', [base, projectName, deployName])

		this.deployPath = sprintf('%srevisions/jenkins-%s-${BUILD_NUMBER}-%s', [this.deployBase, utils.getUnixEpoch(), utils.getRevision()])
		this.debug = debug

		this.shell = new SecureShell()
		this.shell.init(key, host, user, debug)
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
			this.shell.ssh(commands)
		}
	}

	def copy() {
		echo 'Copying files...'

		if(!this.debug) {
			this.shell.scp('./*', deployPath + '/')
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
			this.shell.ssh(commands)
		}
	}
}
