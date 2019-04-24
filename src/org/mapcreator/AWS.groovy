package org.mapcreator

import java.io.Serializable

class AWS implements Serializable {
	@NonCPS
	def getIPS(id, steps) {
		def command = "aws ec2 describe-instances --filters 'Name=tag:Identity,Values=${id}' 'Name=instance-state-code,Values=16' | jq '.Reservations[].Instances[].NetworkInterfaces[].Association.PublicIp"

		def output = steps.sh(returnStdout: true, script: command).trim()
		steps.echo output
	}
}
