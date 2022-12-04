package com.youbook.glowpros.ui.fragment.chat_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.base.BaseFragment
import com.youbook.glowpros.chat_models.AdminItem
import com.youbook.glowpros.chat_models.HeaderInbox
import com.youbook.glowpros.chat_models.InboxResponse
import com.youbook.glowpros.chat_models.UsersItem
import com.youbook.glowpros.databinding.FragmentChatListBinding
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.SocketConnector
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.openNoInternetConnectionActivity
import com.github.nkzawa.emitter.Emitter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class ChatListFragment :
    BaseFragment<ChatMessageViewModel, FragmentChatListBinding, ChatMessageRepository>(),
    View.OnClickListener {
    private var adminList: ArrayList<AdminItem>? = ArrayList()

    //private var driverList: ArrayList<DriversItem>? = ArrayList()
    private var usersList: ArrayList<UsersItem>? = ArrayList()
    private var headerList: ArrayList<HeaderInbox>? = ArrayList()
    private var adapter: InboxHeaderAdapter? = null
    private var firstTime: Boolean = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListener()
        adapter = InboxHeaderAdapter(requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        if (Utils.isConnected(requireContext())) {
            getData()
        } else {
            requireContext().openNoInternetConnectionActivity(requireContext())
        }
    }

    private fun setClickListener() {
        binding.swipeRefresh.setOnRefreshListener { getData() }
    }

    private fun getData() {
        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                emitInbox()
                //binding.progressBar.visibility = View.VISIBLE
                onInbox()
            } else {
                Log.d("TAG", "getData: Socket Not Connected")
            }
        }
    }

    private fun emitInbox() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(
                "token",
                Prefrences.getPreferences(
                    requireContext(),
                    Constants.API_TOKEN
                )
            )
            SocketConnector.getSocket()!!.emit("getInbox", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    // retrieve inbox data
    private fun onInbox() {
        SocketConnector.getSocket()!!.on("setInbox", Emitter.Listener {
            val data = it[0] as JSONObject
            Log.d("TAG", "onInbox: ".plus(data.toString()))
            binding.swipeRefresh.isRefreshing = false
            val inboxResponse: InboxResponse = Gson()
                .fromJson(
                    data.toString(), object : TypeToken<InboxResponse?>() {}.type
                )
            val userList: ArrayList<UsersItem> = ArrayList()
            val adminList: ArrayList<UsersItem> = ArrayList()
            //val driverList: ArrayList<UsersItem> = ArrayList()

            val userMap: MutableMap<String, ArrayList<UsersItem>> = HashMap()
            //val driverMap: MutableMap<String, ArrayList<DriversItem>> = HashMap()
            val adminMap: MutableMap<String, ArrayList<UsersItem>> = HashMap()

            adminList.addAll(inboxResponse.admin as List<UsersItem>)
            for (user in adminList) {
                adminMap[user.role!!] = adminList
            }

            userList.addAll(inboxResponse.users!! as List<UsersItem>)
            for (user in userList) {
                userMap[user.role!!] = userList
            }

            headerList!!.clear()

            for ((key, value) in adminMap.entries) {
                headerList!!.add(HeaderInbox(key, value))
            }
            for ((key, value) in userMap.entries) {
                headerList!!.add(HeaderInbox(key, value))
            }

            if (firstTime)
                notifyList()
        })
    }

    private fun notifyList() {
        if (activity != null) {
            activity?.runOnUiThread {
                binding.recyclerView.recycledViewPool.clear()
                adapter!!.notifyDataSetChanged()
                activity?.runOnUiThread {
                    firstTime = false
                    adapter!!.updateList(headerList as ArrayList<HeaderInbox>)
                    if (headerList!!.size == 0) {
                        binding.tvNoData.visibility = View.VISIBLE
                    } else {
                        binding.tvNoData.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getViewModel() = ChatMessageViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentChatListBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = ChatMessageRepository()

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

}