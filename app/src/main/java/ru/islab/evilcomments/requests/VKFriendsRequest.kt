package ru.islab.evilcomments.requests

import com.vk.api.sdk.exceptions.VKApiCodes
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import ru.islab.evilcomments.domain.VKUser

class VKFriendsRequest(uid: Int = 0) : VKRequest<List<VKUser>>("friends.get") {
    init {
        if (uid != 0) {
            addParam("user_id", uid)
        }
        addParam("order", "hints")
        addParam("count", 10)
        addParam(VKApiCodes.PARAM_LANG, "ru")

        addParam("fields", "photo_200_orig, can_write_private_message, can_see_all_posts, can_post")
    }

    override fun parse(r: JSONObject): List<VKUser> {
        val users = r.getJSONObject("response").getJSONArray("items")
        val result = ArrayList<VKUser>()
        for (i in 0 until users.length()) {
            val vkUser = VKUser.parse(users.getJSONObject(i))
            if (vkUser.canPost) {
                result.add(vkUser)
            }
        }
        return result
    }
}