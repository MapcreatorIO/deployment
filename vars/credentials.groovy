def getPassword(id) { 
    def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
        com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials.class,
        jenkins.model.Jenkins.instance
    )

    def c = creds.findResult { it.id == id ? it : null }

    if ( c ) {
        println "found credential ${c.id} for username ${c.username}"

        def systemCredentialsProvider = jenkins.model.Jenkins.instance.getExtensionList(
            'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
            ).first()

      def password = systemCredentialsProvider.credentials.first().password
    } else {
      println "could not find credential for ${username}"
    }

	return password
}

def call(id) {
	return getPassword(id) 
}
