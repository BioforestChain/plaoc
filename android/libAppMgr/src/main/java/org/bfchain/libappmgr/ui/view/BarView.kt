package org.bfchain.libappmgr.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import org.bfchain.libappmgr.R

@Composable
fun TopBarView(
  backIcon: Int = R.drawable.ic_back,
  onBackAction: () -> Unit,
  content: @Composable RowScope.() -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(android.R.attr.actionBarSize.dp)
  ) {
    /*Image(
      imageVector = ImageVector.vectorResource(id = backIcon),
      contentDescription = "",
      modifier = Modifier
        .padding(4.dp)
        .clickable { onBackAction })*/

    Text(text = "ceshi")
    content
  }
}

@Composable
fun BottomBarView() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(android.R.attr.actionBarSize.dp)
  ) {

  }
}
