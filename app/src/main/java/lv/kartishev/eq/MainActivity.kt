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

    override fun onResume() {
        super.onResume()

        PlaybackEngine.create(this)
    }

    override fun onPause() {

        PlaybackEngine.delete()
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playButton: Button = findViewById(R.id.play_button)
        val eqOnButton: Button = findViewById(R.id.eq_on_button)
        val eqOffButton: Button = findViewById(R.id.eq_off_button)

        eqOnButton.setOnClickListener { v: View ->
            PlaybackEngine.setEQ(true);
        }

        eqOffButton.setOnClickListener { v: View ->
            PlaybackEngine.setEQ(false);
        }

        playButton.setOnClickListener { v: View ->
            if (playing) {
                PlaybackEngine.setToneOn(false)

                playButton.setText(R.string.button_play)

                playing = false
            } else {

                PlaybackEngine.setToneOn(true)

                playButton.setText(R.string.button_stop)

                playing = true
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("hello-oboe")
        }
    }
}
