package org.mapcreator

class GitFile implements Serializable {
    public filename
    public status

    def init(filename, status) {
        this.filename = filename
        this.status = status
    }
}