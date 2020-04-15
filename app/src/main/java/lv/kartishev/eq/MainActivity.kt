package lv.kartishev.eq

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.rm.rmswitch.RMTristateSwitch


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
        val checkButton: Button = findViewById(R.id.check_button)
        val eqScale: EQScale = findViewById(R.id.eq_scale)
        val levelSwitch: RMTristateSwitch = findViewById(R.id.level_switch)
        val levelText: TextView = findViewById(R.id.level_text)

        val resultView: TextView = findViewById(R.id.result)

        levelSwitch.addSwitchObserver { switchView, state ->
            when (state) {
                RMTristateSwitch.STATE_LEFT -> {
                    eqScale.setLevel(Level.EASY)
                    levelText.setText(R.string.level_easy)
                }
                RMTristateSwitch.STATE_MIDDLE -> {
                    eqScale.setLevel(Level.MID)
                    levelText.setText(R.string.level_mid)
                }
                RMTristateSwitch.STATE_RIGHT -> {
                    eqScale.setLevel(Level.HARD)
                    levelText.setText(R.string.level_hard)
                }
            }
        }

        checkButton.setOnClickListener { v: View ->
            val frequency = PlaybackEngine.frequency

            val chosenMinFrequency = eqScale.getBottomRangeFrequency()
            val chosenMaxFrequency = eqScale.getTopRangeFrequency()

            if (frequency >= chosenMinFrequency && frequency <= chosenMaxFrequency) {
                resultView.setText(R.string.result_match)
                eqScale.showCorrect(frequency.toLong())
                v.postDelayed({
                    eqScale.stopShowingCorrect()
                    PlaybackEngine.changeEQ()
                    resultView.setText("")
                }, 2000)
            } else {
                resultView.setText(R.string.result_fail)
                v.postDelayed({
                    resultView.setText("")
                }, 1000)
            }
        }

        eqButton.setOnCheckedChangeListener { _, isChecked ->
            PlaybackEngine.setEQ(isChecked)
        }

        playButton.setOnClickListener {
            if (playing) {
                PlaybackEngine.setToneOn(false)

                playButton.setText(R.string.button_play)

                checkButton.isEnabled = false
                eqButton.isEnabled = false

                playing = false
            } else {
                PlaybackEngine.setToneOn(true)

                playButton.setText(R.string.button_stop)

                checkButton.isEnabled = true
                eqButton.isEnabled = true

                playing = true
            }
        }
    }
}
