package org.mapcreator

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils
import java.io.Serializable

class DeployPHP implements Serializable {
	private String path
	private String base
	private String key
	private String user
	private String host
	private boolean debug
	private ShUtils utils
	private SecureShell shell

	def steps
	DeployPHP(steps) {
		this.steps = steps
	}

	def initialize(String base, String project, String environment, String build,
					String key, String user, String host, Boolean debug = false) {
		this.key = key
		this.host = host
		this.user = host
		this.debug = debug

		this.utils = new ShUtils(this.steps)
		this.shell = new SecureShell(this.steps)
		this.shell.init(key, host, user, debug)

		this.base = sprintf('%s%s/%s/', [base, project, environment])
		this.path = sprintf('%srevisions/jenkins-%s-%s-%s', [this.base, utils.getUnixEpoch(), build, utils.getRevision()])
	}

	def unStash() {
		steps.echo 'Unstashing'

		steps.step([$class: 'WsCleanup'])		
		steps.unstash 'deployable'
	}

	def prepare(List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		commands += sprintf('mkdir -pv %s %s/shared', [this.path, this.base])
		commands += sprintf('cd %s', [this.path])		

		commands += append

		this.shell.ssh(commands)		
	}

	def finish(List shared = [], List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		for(item in shared) {
			commands += sprintf('rm -rv %s/%s || true', [this.path, item])
			commands += sprintf('ln -sv %s/shared/%s %s/%s', [this.base, item, this.path, item])									
		}

		commands += sprintf('rm -v %s/current', [this.base])
 		commands += sprintf('ln -svf %s %s/current', [this.path, this.base])				

 		commands += append

		this.shell.ssh(commands) 		
	}

	def copy(String from) {
		steps.echo "Copying files..."

		this.shell.scp(from, this.path)
	}
}
