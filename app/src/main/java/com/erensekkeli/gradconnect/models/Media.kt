package com.erensekkeli.gradconnect.models

import com.google.firebase.Timestamp
import java.io.Serializable

class Media(val id: String, val email: String?, val title: String?, val description: String?, val mediaUrl: String?, val mediaType: String?, val date: Timestamp?): Serializable {
}