package lv.kartishev.eq.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lv.kartishev.eq.R
import lv.kartishev.eq.components.EQScaleRanged
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

        val turnsType = object : TypeToken<List<Instrument>>() {}.type
        val instruments = Gson().fromJson<List<Instrument>>(loadJSONFromAsset(), turnsType)

        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, instruments.map { it.name })
        val editTextFilledExposedDropdown: AutoCompleteTextView = root.findViewById(R.id.filled_exposed_dropdown)
        editTextFilledExposedDropdown.setAdapter(adapter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            editTextFilledExposedDropdown.setText(instruments[0].name, false)
            eqScale.setRanges(instruments[0].ranges)
        }
        editTextFilledExposedDropdown.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position).toString()
            val instrument = instruments.first { it.name == selectedItem }
            eqScale.setRanges(instrument.ranges)
            rangeTitle.text = ""
            rangeDescription.text = ""
        }

        eqScale.setOnRangeChangedListener { r: Range ->
            rangeTitle.text = "${r.title}: ${r.low}Hz - ${r.high}Hz"
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

data class Range(val low: Long, val high: Long, var title: String, var rangeTitle: String, var description: String, var color: Int = Color.argb(120, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)), var selected: Boolean = false)

data class Instrument(val name: String, val ranges: List<Range>)
