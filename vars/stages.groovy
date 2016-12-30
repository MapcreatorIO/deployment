enum Stages {
	CHECKOUT, INITIALIZE, MIGRATE, TEST, COVERAGE, BUILD, DEPLOY
}


def call() {
	return Stages
}
