@file:OptIn(ExperimentalMaterial3Api::class)

package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.base.BaseFragment
import timber.log.Timber

// TODO: compose-ify this later
@AndroidEntryPoint
class CompoundSearchFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                InputBox()
            }
        }
    }

    @Preview
    @Composable
    fun InputBox() {
        var inputText: String by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(1f)
                    .align(Alignment.CenterStart),
                value = "",
                onValueChange = {
                    inputText = it
                    // Update viewmodel with search query
                },
                singleLine = true,
                label = { Text("Enter your queries one at a time") }
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.25f)
                    .fillMaxHeight(1f)
                    .padding(5.dp),
                onClick = {
                    // update viewmodel to capture query
            }) {
                Text("Add")
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}