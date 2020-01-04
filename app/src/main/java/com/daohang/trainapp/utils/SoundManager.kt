package com.daohang.trainapp.utils

import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import cn.yunzhisheng.tts.offline.basic.ITTSControl
import cn.yunzhisheng.tts.offline.basic.TTSFactory
import cn.yunzhisheng.tts.offline.basic.TTSPlayerListener
import cn.yunzhisheng.tts.offline.common.USCError
import com.daohang.trainapp.MyApplication
import java.util.*

val messageQueue = mutableListOf<String>()
//播报间隔
val speechInterval = 200
var lastSpeechTime = System.currentTimeMillis()

object SoundManager{
    lateinit var handler : Handler
    var context: Context? = null
    var speechUtilOffline: SpeechUtilOffline? = null

    fun initOffline(context: Context){
        this.context = context
        if (speechUtilOffline == null)
            speechUtilOffline = SpeechUtilOffline(context)

        handler = Handler(MyApplication.ttsHandlerthread.looper){
            val content = it.obj.toString()
            speechUtilOffline?.run {
                if (content.isNotEmpty() && content.isNotBlank())
                    play(content)
            }
            true
        }
    }

    fun startPlay(content: String){
        if (speechUtilOffline == null && context != null){
            speechUtilOffline = SpeechUtilOffline(context!!)
            Thread.sleep(1000)
        }

        val msg = Message.obtain()
        msg.obj = content
        handler.sendMessage(msg)
    }


    class SpeechUtilOffline(val context: Context): TTSPlayerListener{
        private val TAG = "SoundManager"
        private val appKey = "_appKey_"
        private var mTTSPlayer: ITTSControl
//        private val executor = Executors.newSingleThreadExecutor()

        var canReadNext = true

        lateinit var task: TimerTask

        init {
            mTTSPlayer = TTSFactory.createTTSControl(context, appKey)
            mTTSPlayer.setTTSListener(this)
            mTTSPlayer.setStreamType(AudioManager.STREAM_MUSIC)
            mTTSPlayer.setVoiceSpeed(5f)
            mTTSPlayer.setVoicePitch(1.1f)
            mTTSPlayer.init()
        }

        fun stop() = mTTSPlayer.stop()

//        fun play(content: String) = executor.execute { mTTSPlayer.play(content) }
        fun play(content: String) = mTTSPlayer.play(content)


        fun startPlay() = Timer().schedule(task,0,2000)

        fun release() = mTTSPlayer.release()


        override fun onCancel() {
            Log.d(TAG, "TTS Cancel")
        }

        override fun onPlayEnd() {
//            messageQueue.removeAt(0)
            canReadNext = true
            Log.d(TAG, "TTS End")
        }

        override fun onInitFinish() {
            Log.d(TAG,"TTS InitFinish")

            task = object : TimerTask() {
                override fun run() {
                    if (canReadNext && messageQueue.isNotEmpty()){
//                        thread {
                            mTTSPlayer.play(messageQueue[0])
//                        }
                    }
                }
            }

            startPlay()
        }

        override fun onPlayBegin() {
            canReadNext = false
            Log.d(TAG,"TTS PlayBegin")
        }

        override fun onBuffer() {
            Log.d(TAG,"TTS Buffer")
        }

        override fun onError(p0: USCError?) {
            Log.d(TAG,"Error: ${p0.toString()}")
        }

    }
}