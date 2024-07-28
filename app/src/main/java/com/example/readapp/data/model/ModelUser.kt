package com.example.readapp.data.model

class ModelUser {
    var email: String = ""
    var name: String = ""
    var profileImage: String = ""
    var timestamp: Long = 0
    var uid: String = ""
    var userType: String = ""

    constructor()
    constructor(email: String, name: String, profileImage: String, timestamp: Long, uid: String, userType: String){
        this.email = email
        this.name = name
        this.profileImage = profileImage
        this.timestamp = timestamp
        this.uid = uid
        this.userType = userType
    }
}