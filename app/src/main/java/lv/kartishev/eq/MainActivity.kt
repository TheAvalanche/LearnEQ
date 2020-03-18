package lv.kartishev.eq

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    val context: Context = this;
    var playing: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mp : MediaPlayer = MediaPlayer.create(context, R.raw.firebird);

        val playButton: Button = findViewById(R.id.play_button)
        val eqOnButton: Button = findViewById(R.id.eq_on_button)
        val eqOffButton: Button = findViewById(R.id.eq_off_button)

        var eq: Equalizer

        eqOnButton.setOnClickListener { v: View ->
            eq = Equalizer(0, mp.audioSessionId)

        }

        playButton.setOnClickListener { v: View ->
            if (playing) {
                if (mp.isPlaying) {
                    mp.stop()
                    mp.release()
                    mp = MediaPlayer.create(context, R.raw.firebird)
                }

                playButton.setText(R.string.button_play)

                playing = false
            } else {

                mp.start()

                playButton.setText(stringFromJNI())

                playing = true
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
