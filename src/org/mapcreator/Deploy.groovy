package org.mapcreator

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils
import java.io.Serializable

class Deploy implements Serializable {
	protected String path
	protected String base
	protected String key
	protected String user
	protected String host
	protected boolean debug
	protected ShUtils utils
	protected SecureShell shell

	def steps
	Deploy(steps) {
		this.steps = steps
	}

	def initialize(String base, String project, String environment, String build,
					String key, String user, String host, Boolean debug = false) {
		this.key = key
		this.host = host
		this.user = user
		this.debug = debug

		this.utils = new ShUtils(this.steps)
		this.shell = new SecureShell(this.steps)
		this.shell.init(key, host, user, debug)

		this.base = sprintf('%s%s/%s/', [base, project, environment])

		if(environment in ['live', 'production']) {
			this.path = sprintf('%srevisions/jenkins-%s-%s', [this.base, utils.getUnixEpoch(), build])
		} else {
			this.path = sprintf('%srevisions/jenkins-%s-%s-%s', [this.base, utils.getUnixEpoch(), build, utils.getRevision()])
		}
	}

	def setHost(String host) {
		this.host = host;
		this.shell.init(this.key, this.host, this.user, this.debug)
	}
	
	def getDeployPath() {
		return this.path
	}
	
	def getBasePath() {
		return this.base
	}

	def unStash() {
		steps.echo 'Unstashing'

		steps.step([$class: 'WsCleanup'])
		steps.unstash 'deployable'
	}

	def prepare(List prepend = [], List append = []) {
		List commands = []
		commands += prepend

		commands += sprintf('sudo /usr/local/bin/limit-revisions %srevisions/ 10', [this.base])
		commands += sprintf('mkdir -pv %s %sshared', [this.path, this.base])
		commands += sprintf('cd %s', [this.path])

		commands += append

		this.shell.ssh(commands)
	}

	def finish(List shared = [], List prepend = [], List append = [], String webUser = 'www-data', String webGroup = 'www-data') {
		List commands = []
		commands += prepend

		for(item in shared) {
			commands += sprintf('rm -rv %s/%s || true', [this.path, item])
			commands += sprintf('ln -sv %sshared/%s %s/%s', [this.base, item, this.path, item])
		}

		commands += sprintf('rm -v %scurrent', [this.base])
 		commands += sprintf('ln -svf %s %scurrent', [this.path, this.base])

		commands += sprintf("cd %s", [this.path])
 		commands += append
		commands += sprintf("sudo chown -R %s:%s %s", [webUser, webGroup, this.path])		

		this.shell.ssh(commands)
	}

	def copy(String from) {
		steps.echo "Copying files..."

		this.shell.scp(from, this.path)
	}

	def untarRelease(url, token, path) {
		this.steps.sh(sprintf("mkdir %s", [path]))
		this.steps.sh(sprintf("wget %s?access_token=%s -O %s/output.tar.gz", [url, token, path]))
		this.steps.sh(sprintf("mkdir %s/temp", [path]))
		this.steps.sh(sprintf("tar xf %s/output.tar.gz -C %s/temp", [path, path]))
		this.steps.sh(sprintf("mkdir %s/output", [path]))
		this.steps.sh(sprintf("cp -R %s/temp/*/. %s/output", [path, path]))
		this.steps.sh(sprintf("rm -rf %s/temp", [path]))

		return "${path}/output"
	}
}
