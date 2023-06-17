package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import greenberg.moviedbshell.state.ProductionDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProductionDetailViewModel(
    productionDetailItem: ProductionDetailItem,
) : ViewModel() {

    private val _productionDetailState = MutableStateFlow(ProductionDetailState(productionDetailItem))
    val productionDetailState = _productionDetailState.asStateFlow()

    // TODO: I don't think anything else needs to be done here for now but keep my options open anyway

    companion object {
        fun provideFactory(productionDetailItem: ProductionDetailItem) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductionDetailViewModel(productionDetailItem) as T
            }
        }
    }
}
