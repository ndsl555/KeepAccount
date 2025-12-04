import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.keepaccount.UseCase.GetItemsByDateUseCase
import com.example.keepaccount.Utils.Result
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DayPieViewModel(
    private val getItemsByDateUseCase: GetItemsByDateUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PieUIState())
    val uiState: StateFlow<PieUIState> = _uiState.asStateFlow()

    fun loadTodayData(
        thisYear: String,
        thisMonth: String,
        thisDay: String,
    ) {
        viewModelScope.launch {
            when (val result = getItemsByDateUseCase.invoke(GetItemsByDateUseCase.Parameters(thisYear, thisMonth, thisDay))) {
                is Result.Success -> {
                    val grouped = result.data.groupBy { it.itemName }
                    val totalCost = result.data.sumOf { it.itemPrice.toInt() }

                    val mergedList =
                        grouped.map { (name, list) ->
                            ExampleItem(
                                itemname = name,
                                itemcost = list.sumOf { it.itemPrice.toInt() },
                            )
                        }

                    val pieEntries = mergedList.map { PieEntry(it.itemcost.toFloat(), it.itemname) }
                    val colors =
                        mergedList.map { item ->
                            grouped[item.itemname]!!.first().itemColorcode.toColorInt()
                        }
                    _uiState.value =
                        PieUIState(
                            totalCost = totalCost,
                            pieEntries = pieEntries,
                            pieColors = colors,
                            todayItems = mergedList,
                        )
                }

                is Result.Error -> {
                    // 可加錯誤處理
                    _uiState.value = PieUIState()
                }
            }
        }
    }
}

data class PieUIState(
    val totalCost: Int = 0,
    val pieEntries: List<PieEntry> = emptyList(),
    val pieColors: List<Int> = emptyList(),
    val todayItems: List<ExampleItem> = emptyList(),
)

data class ExampleItem(val itemname: String, val itemcost: Int)
