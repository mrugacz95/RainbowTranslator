package pl.poznan.put.rainbowtranslator.color

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_color.*
import org.json.JSONArray
import org.json.JSONObject
import pl.poznan.put.rainbowtranslator.R
import pl.poznan.put.rainbowtranslator.model.ColorData
import pl.poznan.put.rainbowtranslator.search.SearchActivity
import java.net.URI


class ColorActivity : AppCompatActivity(), HistoryAdapter.OnClickListener {

    private lateinit var socket: Socket
    private lateinit var historyAdapter: HistoryAdapter
    private var currentColor: ColorData? = null

    companion object {
        val SELECT = "select"
        val DELETE = "delete"
        val DELETE_ALL = "deleteAll"
        val TOGGLE_LIGHT = "click"
        val INSERT = "insert"
        val RGB = "rgb"
        val COLOR = "color"
        val COLORS = "colors"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)
        historyAdapter = HistoryAdapter(arrayListOf(), this)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        openSocket()
        ibToggleLight.setOnClickListener {
            socket.emit(TOGGLE_LIGHT, arrayListOf<String>())
        }
        ibDeleteAll.setOnClickListener {
            socket.emit(DELETE_ALL, arrayListOf<String>())
            historyAdapter.setData(arrayListOf())
        }
        fabInsertColor.setOnClickListener {
            val tmpColor = currentColor
            if (tmpColor != null) {
                socket.emit(INSERT, tmpColor.toJson())
                historyAdapter.addData(tmpColor)
            }
        }
        srl_refresh.setOnRefreshListener {
            socket.emit(SELECT, arrayListOf<String>())
        }
    }

    override fun onDestroy() {
        socket.disconnect()
        super.onDestroy()
    }

    private fun openSocket() {
        val domain = intent.getStringExtra(SearchActivity.ARG_DOMAIN)
        val port = intent.getIntExtra(SearchActivity.ARG_PORT, 80)
        val uri = URI(getString(R.string.socket_address, domain, port))
        socket = IO.socket(uri)
        socket.connect()
        socket.emit(SELECT, arrayListOf<String>())
        socket.on(COLOR) { args -> onColor(args) }
        socket.on(COLORS) { args -> onColorList(args) }
    }

    override fun onClick(color: ColorData) {
        socket.emit(DELETE, color.toJson())
    }

    private fun onColor(args: Array<out Any>) = run {
        Observable.just(args)
                .map { args[0] as JSONObject }
                .map { it -> ColorData(it.getString(COLOR), it.getString(RGB)) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { color ->
                            currentColor = color
                            vColor.setBackgroundColor(Color.parseColor(color.rgb))
                            tvColorName.text = color.color
                        }
                )
    }

    private fun onColorList(args: Array<out Any>) {
        Observable.just(args)
                .map { it[0] as JSONArray }
                .map {
                    val colors: ArrayList<ColorData> = ArrayList()
                    (0..(it.length() - 1))
                            .map { i -> it.getJSONObject(i) }
                            .mapTo(colors) { ColorData(it.getString(COLOR), it.getString(RGB)) }
                    Observable.just(colors)
                }
                .flatMap { it -> it }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    historyAdapter.setData(it)
                    srl_refresh.isRefreshing = false
                }
    }

}
