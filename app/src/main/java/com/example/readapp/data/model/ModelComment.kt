package com.example.readapp.data.model

class ModelComment {
    var id = ""
    var bookId = ""
    var timestamp = ""
    var uid = ""
    var comment = ""


    //param constructor
    constructor(id: String, bookId: String, timestamp: String, uid: String, comment: String) {
        this.id = id
        this.bookId = bookId
        this.timestamp = timestamp
        this.uid = uid
        this.comment = comment

    }
}