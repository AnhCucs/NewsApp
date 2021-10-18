package anhcucs.ninhgiang_hd.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import anhcucs.ninhgiang_hd.newsapp.repository.NewRepository

class NewViewModelFactory(
    val newRepository: NewRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewViewModel(newRepository) as T
    }
}