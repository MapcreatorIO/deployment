def getIPsFromID(id, steps) {
	def command = "aws ec2 describe-instances --filters 'Name=tag:Identity,Values=${id}' 'Name=instance-state-code,Values=16' | jq '.Reservations[].Instances[].NetworkInterfaces[].Association.PublicIp"

	def output = steps.sh(returnStdout: true, script: command).trim()
	steps.echo output
}

def call(id, steps) {
	return getIpsFromID(id, steps)
}
// vim: set ft=groovy:
