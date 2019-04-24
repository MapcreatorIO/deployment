package org.mapcreator

import java.io.Serializable

class AWS implements Serializable {
	def getIPS(id, steps) {
		def command = "aws ec2 describe-instances --filters 'Name=tag:Identity,Values=${id}' 'Name=instance-state-code,Values=16' | jq '.Reservations[].Instances[].NetworkInterfaces[].Association.PublicIp'"

		return steps.sh(returnStdout: true, script: command).trim().split('\n')
	}
}
