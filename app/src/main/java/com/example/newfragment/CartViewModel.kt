import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel : ViewModel() {
    // LiveData для лічильника товарів у кошику
    val cartCount = MutableLiveData<Int>()
}
