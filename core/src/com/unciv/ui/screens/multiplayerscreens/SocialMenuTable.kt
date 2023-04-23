package com.unciv.ui.screens.multiplayerscreens

import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.logic.multiplayer.apiv2.AccountResponse
import com.unciv.ui.popups.InfoPopup
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.utils.Log
import com.unciv.utils.concurrency.Concurrency
import kotlinx.coroutines.delay
import java.util.*

class SocialMenuTable(
    private val base: BaseScreen,
    me: UUID,
    maxChatHeight: Float = 0.8f * base.stage.height
): Table(BaseScreen.skin) {

    internal val friendList = FriendListV2(
        base,
        me,
        requests = true,
        chat = { _, a, c -> startChatting(a, c) },
        edit = { f, a -> FriendListV2.showRemoveFriendshipPopup(f, a, base) }
    )
    private val chatContainer = Container<ChatTable>()
    private var lastSelectedFriendChat: UUID? = null

    init {
        add(friendList).growX()
        add(chatContainer).maxHeight(maxChatHeight)
        Concurrency.run {
            while (stage == null) {
                delay(10)
            }
            InfoPopup.wrap(stage) { friendList.triggerUpdate() }
        }
    }

    private fun startChatting(friend: AccountResponse, chatRoom: UUID) {
        if (lastSelectedFriendChat == chatRoom) {
            chatContainer.actor?.dispose()
            chatContainer.actor = null
            lastSelectedFriendChat = null
            return
        }
        lastSelectedFriendChat = chatRoom
        Log.debug("Opening chat dialog with friend %s (room %s)", friend, chatRoom)
        chatContainer.actor?.dispose()
        chatContainer.actor = ChatTable(
            ChatMessageList(chatRoom, base.game.onlineMultiplayer),
            false
        ).apply { padLeft(15f) }
    }

}