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
                dismiss()
                val userInfo = partyUserInfoMenu[position].values.firstOrNull()
                val key = partyUserInfoMenu[position].keys.first()
                adapter.confirmDialog(userInfo!!.nickName,key)
            }
        }
    }

}