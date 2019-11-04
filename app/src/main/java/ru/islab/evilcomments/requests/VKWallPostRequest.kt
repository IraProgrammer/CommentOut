package ru.islab.evilcomments.requests

import com.vk.api.sdk.exceptions.VKApiCodes
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.islab.evilcomments.domain.VKUser

class VKWallPostRequest() : VKRequest<String>("wall.post") {
    init {
        addParam("owner_id", 83144073)
        addParam("order", "hints")
        addParam("message", "ня")
    }

    override fun parse(r: JSONObject): String {
        val dr = r
        return ""
    }
}