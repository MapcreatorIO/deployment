package org.mapcreator

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils
import java.io.Serializable

class DeployPHP implements Serializable {
	protected String path
	protected String base
	protected String key
	protected String user
	protected String host
	protected boolean debug
	protected ShUtils utils
	protected SecureShell shell

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

		if(environment == 'live') {
			this.path = sprintf('%srevisions/jenkins-%s-%s', [this.base, utils.getUnixEpoch(), build])
		} else {
			this.path = sprintf('%srevisions/jenkins-%s-%s-%s', [this.base, utils.getUnixEpoch(), build, utils.getRevision()])
		}
	}

	def unStash() {
		steps.echo 'Unstashing'

		steps.step([$class: 'WsCleanup'])		
		steps.unstash 'deployable'
	}

	def prepare(List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		commands += sprintf('mkdir -pv %s %sshared', [this.path, this.base])
		commands += sprintf('cd %s', [this.path])		

		commands += append

		this.shell.ssh(commands)		
	}

	def finish(List shared = [], List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		for(item in shared) {
			commands += sprintf('rm -rv %s/%s || true', [this.path, item])
			commands += sprintf('ln -sv %sshared/%s %s/%s', [this.base, item, this.path, item])									
		}

		commands += sprintf('rm -v %scurrent', [this.base])
 		commands += sprintf('ln -svf %s %scurrent', [this.path, this.base])				

 		commands += append

		this.shell.ssh(commands) 		
	}

	def copy(String from) {
		steps.echo "Copying files..."

		this.shell.scp(from, this.path)
	}

	def untarRelease(url, token, path) {
		this.utils.sh("mkdir ${path}")
		this.utils.sh("wget ${url}?access_token=${token} -O ${path}/output.tar.gz")
		this.utils.sh("mkdir ${path}/temp")
		this.utils.sh("tar xf ${path}/output.tar.gz -C ${path}/temp")
		this.utils.sh("mkdir ${path}/output")
		this.utils.sh("mv ${path}/temp/*/* ${path}/output")

		return "${path}/output"
	}
}
