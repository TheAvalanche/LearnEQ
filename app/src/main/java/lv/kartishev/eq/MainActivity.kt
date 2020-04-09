package lv.kartishev.eq

import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
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
        val eqButton: ToggleButton = findViewById(R.id.eq_button)
        val bassButton: Button = findViewById(R.id.bass_button)
        val midButton: Button = findViewById(R.id.mid_button)
        val highButton: Button = findViewById(R.id.high_button)

        val resultView: TextView = findViewById(R.id.result)

        bassButton.setOnClickListener { v: View ->
            val frequency = PlaybackEngine.frequency

            if (frequency >= 50 && frequency <= 250) {
                resultView.setText("Match")
                v.postDelayed({
                    PlaybackEngine.changeEQ()
                    resultView.setText("")
                }, 1000)
            } else {
                resultView.setText("Fail")
            }
        }

        midButton.setOnClickListener { v: View ->
            val frequency = PlaybackEngine.frequency

            if (frequency >= 250 && frequency <= 4000) {
                resultView.setText("Match")
                v.postDelayed({
                    PlaybackEngine.changeEQ()
                    resultView.setText("")
                }, 1000)
            } else {
                resultView.setText("Fail")
            }
        }

        highButton.setOnClickListener { v: View ->
            val frequency = PlaybackEngine.frequency

            if (frequency >= 4000 && frequency <= 20000) {
                resultView.setText("Match")
                v.postDelayed({
                    PlaybackEngine.changeEQ()
                    resultView.setText("")
                }, 1000)
            } else {
                resultView.setText("Fail")
            }
        }

        eqButton.setOnCheckedChangeListener { _, isChecked ->
            PlaybackEngine.setEQ(isChecked)
        }

        playButton.setOnClickListener {
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
}
