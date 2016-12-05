package org.mapcreator;

import org.mapcreator.SecureShell
import org.mapcreator.ShUtils
import java.io.Serializable
import org.mapcreator.DeployPHP

class DeployArt extends DeployPHP {
	def prepare(List prepend = [], List append = []) {
		this.steps.echo 'Installing composer'
		this.steps.sh 'composer install --quiet --no-dev'

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
		
		commands += sprintf('cd %s', [this.path])
		commands += 'php artisan migrate'
		commands += 'php artisan route:cache'
		commands += 'php artisan config:cache'

		commands += sprintf('rm -v %scurrent', [this.base])
		commands += sprintf('ln -svf %s %scurrent', [this.path, this.base])

		commands += append

		this.shell.ssh(commands)
	}
}
