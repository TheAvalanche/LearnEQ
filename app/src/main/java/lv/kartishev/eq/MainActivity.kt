package lv.kartishev.eq

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.rm.rmswitch.RMTristateSwitch


class MainActivity : AppCompatActivity() {

    val context: Context = this;
    var playing: Boolean = false;
    lateinit var mAdView: AdView;

    override fun onResume() {
        super.onResume()
        PlaybackEngine.create(this) //todo
        mAdView.resume()
    }

    override fun onPause() {
        mAdView.pause()
        PlaybackEngine.delete() //todo
        super.onPause()
    }

    public override fun onDestroy() {
        mAdView.destroy()

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        MobileAds.initialize(this) { }

        mAdView = findViewById(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val playButton: MaterialButton = findViewById(R.id.play_button)
        val eqSwitch: MaterialButtonToggleGroup = findViewById(R.id.eq_switch)
        val eqOffButton: Button = findViewById(R.id.eq_off_button)
        val eqOnButton: Button = findViewById(R.id.eq_on_button)
        val checkButton: Button = findViewById(R.id.check_button)
        val eqScale: EQScale = findViewById(R.id.eq_scale)
        val levelSwitch: RMTristateSwitch = findViewById(R.id.level_switch)
        val levelText: TextView = findViewById(R.id.level_text)

        val resultView: TextView = findViewById(R.id.result)

        levelSwitch.addSwitchObserver { _, state ->
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
                    eqSwitch.check(R.id.eq_on_button)
                    PlaybackEngine.changeEQ()
                    PlaybackEngine.setEQ(true)
                    resultView.setText("")
                }, 2000)
            } else {
                resultView.setText(R.string.result_fail)
                v.postDelayed({
                    eqSwitch.check(R.id.eq_on_button)
                    PlaybackEngine.setEQ(true)
                    resultView.setText("")
                }, 1000)
            }
        }

        eqOffButton.setOnClickListener {
            PlaybackEngine.setEQ(false)
        }

        eqOnButton.setOnClickListener {
            PlaybackEngine.setEQ(true)
        }

        playButton.setOnClickListener {
            if (playing) {
                PlaybackEngine.setToneOn(false)

                playButton.setText(R.string.button_play)
                playButton.setIconResource(R.drawable.baseline_play_arrow_white_24dp)

                checkButton.isEnabled = false

                playing = false
            } else {
                PlaybackEngine.setToneOn(true)

                playButton.setText(R.string.button_stop)
                playButton.setIconResource(R.drawable.baseline_stop_white_24dp)

                checkButton.isEnabled = true

                playing = true
            }
        }
    }
}
