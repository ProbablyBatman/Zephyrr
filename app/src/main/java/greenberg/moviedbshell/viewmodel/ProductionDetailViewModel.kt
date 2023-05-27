package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import greenberg.moviedbshell.state.ProductionDetailState

class ProductionDetailViewModel(
    state: ProductionDetailState
//) : ZephyrrMvRxViewModel<ProductionDetailState>(state)
) : ViewModel()
