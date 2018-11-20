package com.example.essam.firebaserecycleviewpagination

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.essam.firebaserecycleviewpagination.Adapter.UserAdapter
import com.example.essam.firebaserecycleviewpagination.Model.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val ITEM_COUNT = 21
    var totla_item = 0
    var last_visible_item = 0
    lateinit var adapter: UserAdapter
    var isLoading = false
    var isMaxData = false
    var last_node: String? = ""
    var last_key: String? = ""

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id = item!!.itemId
        if (id == R.id.refresh) {
            isMaxData = false
            last_node = adapter.lastItemId
            adapter.removeLastItem()
            adapter.notifyDataSetChanged()
            getLastKey()
            fetcData()
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.setTitle("Kotlin Recycle Paging")

        getLastKey()

        var layoutManager = LinearLayoutManager(this)
        recycle_user.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(recycle_user.context, layoutManager.orientation)
        recycle_user.addItemDecoration(dividerItemDecoration)
        adapter = UserAdapter(this)
        recycle_user.adapter = adapter

        fetcData()

        recycle_user.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totla_item = layoutManager.itemCount
                last_visible_item = layoutManager.findLastVisibleItemPosition()
                if (!isLoading && totla_item <= last_visible_item + ITEM_COUNT) {
                    fetcData()
                    isLoading = true
                }
            }
        })
    }

    private fun fetcData() {
        if (!isMaxData) {
            var query: Query = if (TextUtils.isEmpty(last_node)) {
                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .orderByKey()
                    .limitToFirst(ITEM_COUNT)

            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .startAt(last_node)
                    .orderByKey()
                    .limitToFirst(ITEM_COUNT)
            }
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(this@MainActivity, p0.message, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        var newUser = ArrayList<User>()
                        for (snapShot in p0.children) {
                            newUser.add(snapShot.getValue(User::class.java)!!)
                        }
                        last_node = newUser[newUser.size - 1].id
                        if (!last_node.equals(last_key)) {
                            newUser.removeAt(newUser.size - 1)
                        } else
                            last_node = "end"
                        adapter.addAll(newUser)
                        isLoading = false


                    } else {
                        isLoading = false
                        isMaxData = true
                    }
                }

            })
        }

    }

    private fun getLastKey() {
        val get_last_key = FirebaseDatabase.getInstance().getReference()
            .child("User")
            .orderByKey()
            .limitToLast(1)
        get_last_key.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (userSnapShot in p0.children)
                    last_key = userSnapShot.key
            }

        })
    }
}
