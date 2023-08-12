package com.bapool.bapool.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.SelectPartyLeaderAdapter
import com.bapool.bapool.retrofit.data.FirebasePartyMessage
import com.bapool.bapool.retrofit.data.FirebaseUserInfo
import com.bapool.bapool.ui.LoginActivity.Companion.UserId

class SelectPartyLeaderDialog(context: Context) : Dialog(context) {

    private var title: String = ""
    private var onCloseClickListener: (() -> Unit)? = null
    private var partyUserInfoMenu: ArrayList<Map<String, FirebaseUserInfo>> = arrayListOf()
    private var partyId: String = ""

    fun setTitle(title: String) {
        this.title = title
    }

    fun setOnCloseClickListener(listener: () -> Unit) {
        onCloseClickListener = listener
    }

    fun setPartyUserInfoMenu(partyUserInfoMenu: ArrayList<Map<String, FirebaseUserInfo>>) {
        this.partyUserInfoMenu = partyUserInfoMenu
    }

    fun setPartyId(partyId: String) {
        this.partyId = partyId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_partyleader_dialog)



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = SelectPartyLeaderAdapter(
            context,
            partyUserInfoMenu,
            UserId.toString(),
            partyId
        )


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter.itemClick = object : SelectPartyLeaderAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                Toast.makeText(context, "adsfasfasdf", Toast.LENGTH_SHORT).show()
                dismiss()
                val userInfo = partyUserInfoMenu[position].values.firstOrNull()
                val key = partyUserInfoMenu[position].keys.first()
                adapter.confirmDialog(userInfo!!.nickName,key)
            }
        }
    }
//
//    //파티장 바뀌었다고 채팅창에 알림  fcm X
//    fun sendNotificationChangePartyLeader() {
//        var partyLeaderNickName = partyUserInfo[groupOnerId]?.nickName
//        val notificationText = "파티장이 ${partyLeaderNickName}로 바뀌었습니다"
//        var items = mutableListOf<String>()
//        for (data in partyUserInfo.values) {
//            items.add(data.firebaseToken.toString())
//        }
//        if (notificationText != "") {
//            val group_messages =
//                FirebasePartyMessage("공지", getTime(), notificationText, 2)
//            database.child("test").child("Groups").child(partyId.toString()).child("groupMessages")
//                .push()
//                .setValue(group_messages)
//            for (data in items) {
//                sendNotificationFcm(data, notificationText)
//                Log.d("asdfsdfsadasdf", data)
//            }
//        }
//    }



}