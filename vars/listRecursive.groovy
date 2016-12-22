def listRecursive(File loc, ext) {
	def files = []

	for(file in loc.listFiles()) {
		if(file.isFile()) {
			if(file.getName().endsWith(ext)) {
				files << file.getName()
			}
		} else {
			files += listRecursive(file, ext)
		}
	}

	return files
}

def call(loc, ext) {
	return listRecursive(loc, ext)
}
