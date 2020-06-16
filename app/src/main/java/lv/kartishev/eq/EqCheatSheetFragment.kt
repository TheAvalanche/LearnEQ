package lv.kartishev.eq

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.random.Random

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

class EqCheatSheetFragment : Fragment() {



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_eq_cheat_sheet, container, false)

        val eqScale: EQScaleRanged = root.findViewById(R.id.eq_scale_ranged)
        val rangeTitle: TextView = root.findViewById(R.id.range_title)
        val rangeDescription: TextView = root.findViewById(R.id.range_description)
        val sheetSwitch: MaterialButtonToggleGroup = root.findViewById(R.id.sheet_switch)

        val turnsType = object : TypeToken<List<Instrument>>() {}.type
        val instruments = Gson().fromJson<List<Instrument>>(loadJSONFromAsset(), turnsType)

        for (instrument in instruments) {
            val btnTag = MaterialButton(requireActivity(), null, R.attr.materialButtonOutlinedStyle)
            btnTag.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btnTag.text = instrument.name
            btnTag.setPadding(0)
            btnTag.setOnClickListener { eqScale.setRanges(instrument.ranges) }

            sheetSwitch.addView(btnTag)
        }



        eqScale.setOnRangeChangedListener { r: Range ->
            rangeTitle.text = "${r.name}: ${r.low}Hz - ${r.high}Hz"
            rangeDescription.text = r.description
        }


        return root
    }

    fun loadJSONFromAsset(): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = requireActivity().assets.open("eq_ranges.json")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }
}

data class Range(val low: Long, val high: Long, var name: String, var description: String, var color: Int = Color.argb(120, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)), var selected: Boolean = false)

data class Instrument(val name: String, val ranges: List<Range>)
