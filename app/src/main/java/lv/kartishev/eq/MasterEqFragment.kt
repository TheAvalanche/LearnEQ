package lv.kartishev.eq

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.rm.rmswitch.RMTristateSwitch


class MasterEqFragment : Fragment() {

    var playing: Boolean = false;

    var mPluginReceiver: BroadcastReceiver = PluginBroadcastReceiver()

    lateinit var playButton: MaterialButton;
    lateinit var checkButton: MaterialButton;

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        requireActivity().registerReceiver(mPluginReceiver, filter)
        PlaybackEngine.create(requireActivity()) //todo
    }

    override fun onPause() {
        requireActivity().unregisterReceiver(mPluginReceiver);
        PlaybackEngine.delete() //todo
        super.onPause()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_master_eq, container, false)

        playButton = root.findViewById(R.id.play_button)
        checkButton = root.findViewById(R.id.check_button)
        val eqSwitch: MaterialButtonToggleGroup = root.findViewById(R.id.eq_switch)
        val eqOffButton: Button = root.findViewById(R.id.eq_off_button)
        val eqOnButton: Button = root.findViewById(R.id.eq_on_button)
        val eqScale: EQScale = root.findViewById(R.id.eq_scale)
        val levelSwitch: RMTristateSwitch = root.findViewById(R.id.level_switch)
        val levelText: TextView = root.findViewById(R.id.level_text)

        val resultView: TextView = root.findViewById(R.id.result)

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
                resultView.setTextColor(resources.getColor(R.color.colorAccent))
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
                resultView.setTextColor(resources.getColor(R.color.colorPrimaryDark))
                v.postDelayed({
                    //eqSwitch.check(R.id.eq_on_button)
                    //PlaybackEngine.setEQ(true)
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

        return root
    }

    inner class PluginBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            PlaybackEngine.setToneOn(false)
            playButton.setText(R.string.button_play)
            playButton.setIconResource(R.drawable.baseline_play_arrow_white_24dp)

            checkButton.isEnabled = false

            playing = false
            PlaybackEngine.delete()

            PlaybackEngine.create(requireActivity())
        }
    }
}
