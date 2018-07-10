package org.mapcreator
import org.mapcreator.Deploy

class DeployArt extends Deploy {
	DeployArt(steps) {
		super(steps)
	}

	@Override
	def prepare(List prepend = [], List append = []) {
		this.steps.echo 'Installing composer'	
		this.steps.sh 'composer install --no-interaction --no-progress --optimize-autoloader --no-suggest --no-dev'	
		this.steps.sh 'composer dump-autoload --no-dev'
		
		List commands = []
		commands += prepend

		commands += sprintf('sudo /usr/local/bin/limit-revisions %srevisions/ 10', [this.base])
		commands += sprintf('mkdir -pv %s %sshared', [this.path, this.base])
		commands += sprintf('cd %s', [this.path])

		commands += append

		this.shell.ssh(commands)
	}

	@Override
	def finish(List shared = [], List prepend = [], List append = [], String webUser = 'www-data', String webGroup = 'www-data', Boolean migrate=true) {
		List commands = []
		commands += prepend

		for(item in shared) {
			commands += sprintf('rm -rv %s/%s || true', [this.path, item])
			commands += sprintf('ln -sv %sshared/%s %s/%s', [this.base, item, this.path, item])
		}

		commands += sprintf('cd %s', [this.path])

		commands += 'php artisan down'

		if(migrate) {			
			commands += 'php artisan migrate'
		}

		commands += 'php artisan route:cache'
		commands += 'php artisan config:cache'

		commands += sprintf('rm -v %scurrent', [this.base])
		commands += sprintf('ln -svf %s %scurrent', [this.path, this.base])

		commands += append
		commands += 'php artisan up'

		commands += sprintf("sudo chown -R %s:%s %s", [webUser, webGroup, this.path])

		this.shell.ssh(commands)
	}
}
