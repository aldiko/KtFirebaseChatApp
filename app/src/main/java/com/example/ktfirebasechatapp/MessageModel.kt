package com.example.ktfirebasechatapp

class MessageModel {
    var text: String? = null
    var name: String? = null
    var imageurl: String? = null

    constructor() {}
    constructor(text: String?, name: String?, imageurl: String?) {
        this.text = text
        this.name = name
        this.imageurl = imageurl
    }
}