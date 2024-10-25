package com.example.flowdemo.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flowdemo.data.Post
import com.example.flowdemo.repository.DataRepository
import com.example.flowdemo.ui.theme.FlowDemoTheme
import com.example.flowdemo.ui.viewmodels.MainViewModel
import com.example.flowdemo.ui.viewmodels.MainViewModelFactory
import kotlinx.coroutines.Dispatchers

private val TAG = MainActivity::class.java.simpleName

class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainViewModelFactory: MainViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            mainViewModelFactory = MainViewModelFactory(DataRepository(Dispatchers.IO))
            mainViewModel =
                ViewModelProvider(this, mainViewModelFactory).get(MainViewModel::class.java)

            FlowDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        mainViewModel = mainViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                onClick = {
//                    mainViewModel.getDataFromInternet()
                    mainViewModel.getDataFlowFromInternet()
                }
            ) {
                Text(text = "getDataFromInternet()")
            }
            val postsList = mainViewModel.data.collectAsState().value
            val error = mainViewModel.error.collectAsState().value

            if (error == null && postsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .height(300.dp)
                        .width(250.dp)
                ) {
                    items(postsList) { post ->
                        PostListItemView(
                            post = post,
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                }
            }
            if (mainViewModel.postData.collectAsState().value.toString() != "null") {
                Text(
                    text = mainViewModel.postData.collectAsState().value.toString()
                )
            }
            Text(
                text = "Hello $name!",
                modifier = modifier
            )
            Text(
                //Dont know how to use the initialValue here.
                text = "Count Down Timer : ${
                    mainViewModel.countDownTimerDemo.collectAsStateWithLifecycle(
                        25
                    ).value
                }",
                modifier = modifier
            )
            Text(
                text = "Count Counter : ${mainViewModel.count.collectAsState().value}",
                modifier = modifier
            )
            Button(
                onClick = { mainViewModel.updateCount() },
                modifier = modifier
            ) {
                Text(text = "Counter ++")
            }
            if (mainViewModel.showProgress.collectAsState().value) {
                CircularProgressIndicator(
                    color = Color.Magenta,
                    strokeWidth = 5.dp,
                    trackColor = Color.LightGray,
                )
            }
            Button(
                onClick = { mainViewModel.toggleProgress() }
            ) {
                Text(text = "Show/Hide Progress")
            }
            Button(
                onClick = {
                    mainViewModel.getSpecificPost()
                }
            ) {
                Text(text = "getPost()")
            }
        }
    }
}

@Composable
fun PostListItemView(
    post: Post,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
    ) {
        Text(
            text = post.id.toString()
        )
        Text(
            text = post.title
        )
        Text(
            text = post.userId.toString()
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    Greeting(
        name = "Android",
        mainViewModel = MainViewModel(DataRepository(Dispatchers.IO))
    )
}