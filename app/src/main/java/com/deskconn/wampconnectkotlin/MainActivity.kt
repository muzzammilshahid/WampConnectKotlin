package com.deskconn.wampconnectkotlin

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.crossbar.autobahn.wamp.Client
import io.crossbar.autobahn.wamp.Session
import io.crossbar.autobahn.wamp.types.ExitInfo
import io.crossbar.autobahn.wamp.types.SessionDetails
import io.crossbar.autobahn.wamp.types.Subscription
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    var status: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.status)
        subscribe()
    }

    private fun subscribe() {
        val wampSession = Session()
        wampSession.addOnJoinListener { session: Session, details: SessionDetails ->
            onJoinSubscribe(
                session
            )
        }
        wampSession.addOnDisconnectListener { session: Session, wasClean: Boolean ->
            onDisconnect()
        }
        val client = Client(wampSession, "ws://192.168.100.116:8080/ws", "realm1")
        client.connect().whenComplete { exitInfo: ExitInfo?, throwable: Throwable? ->
            println(
                "Exit!"
            )
        }
    }

    private fun onJoinSubscribe(session: Session) {
        println("Joined realm")
        status!!.background = resources.getDrawable(R.drawable.status_ok)
        val future = session.subscribe(
            "pk.codebase.test",
            Consumer { items: List<Any> ->
                onData(
                    items
                )
            })
        future.thenAccept { subscription: Subscription? -> }
        future.exceptionally { throwable: Throwable ->
            throwable.printStackTrace()
            null
        }
    }

    private fun onDisconnect() {
        status!!.background = resources.getDrawable(R.drawable.ic_status)
    }

    private fun onData(items: List<Any>) {
        println(items)
        Toast.makeText(this, "" + items, Toast.LENGTH_SHORT).show()
    }

}