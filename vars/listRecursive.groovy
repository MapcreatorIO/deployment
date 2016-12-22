def listRecursive(loc, ext) {
	def files = []

	for(file in loc.listFiles()) {
		if(file.isFile()) {
			if(file.getName().endsWith(ext)) {
				files << file.getName()
			}
		} else {
			files += getFileS(file)
		}
	}

	return files
}

def call(loc, ext) {
	return listRecursive(loc, ext)
}
