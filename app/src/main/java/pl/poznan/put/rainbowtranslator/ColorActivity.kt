package pl.poznan.put.rainbowtranslator

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_color.*
import org.json.JSONObject
import pl.poznan.put.rainbowtranslator.model.ColorData
import java.net.URI


class ColorActivity : AppCompatActivity() {
    private lateinit var socket: Socket

    companion object {
        val TAG: String = ColorActivity::class.java.simpleName
        val RESPONSE = "response"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        val domain = intent.getStringExtra(SearchActivity.ARG_DOMAIN)
        val port = intent.getIntExtra(SearchActivity.ARG_PORT, 80)
        val uri = URI(getString(R.string.socket_address, domain, port))
        socket = IO.socket(uri)
        socket.connect()
        socket.emit("request", arrayListOf<String>())
        socket.on(RESPONSE) { args ->
            run {
                Observable.just(args)
                        .map { args[0] as JSONObject }
                        .map { it -> ColorData(it.getString("color"), it.getString("rgb")) }
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { color ->
                                    vColor.setBackgroundColor(Color.parseColor("#00ff00"))
                                    tvColorName.text = color.color
                                }
                        )
            }
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }

}
