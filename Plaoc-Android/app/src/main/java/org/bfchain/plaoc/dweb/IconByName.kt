package org.bfchain.plaoc.dweb

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

private val materialSystemFilledIcons = mapOf(
    "AccountBox" to Icons.Filled.AccountBox,
    "AccountCircle" to Icons.Filled.AccountCircle,
    "Add" to Icons.Filled.Add,
    "AddCircle" to Icons.Filled.AddCircle,
    "ArrowBack" to Icons.Filled.ArrowBack,
    "ArrowDropDown" to Icons.Filled.ArrowDropDown,
    "ArrowForward" to Icons.Filled.ArrowForward,
    "Build" to Icons.Filled.Build,
    "Call" to Icons.Filled.Call,
    "Check" to Icons.Filled.Check,
    "CheckCircle" to Icons.Filled.CheckCircle,
    "Clear" to Icons.Filled.Clear,
    "Close" to Icons.Filled.Close,
    "Create" to Icons.Filled.Create,
    "DateRange" to Icons.Filled.DateRange,
    "Delete" to Icons.Filled.Delete,
    "Done" to Icons.Filled.Done,
    "Edit" to Icons.Filled.Edit,
    "Email" to Icons.Filled.Email,
    "ExitToApp" to Icons.Filled.ExitToApp,
    "Face" to Icons.Filled.Face,
    "Favorite" to Icons.Filled.Favorite,
    "FavoriteBorder" to Icons.Filled.FavoriteBorder,
    "Home" to Icons.Filled.Home,
    "Info" to Icons.Filled.Info,
    "KeyboardArrowDown" to Icons.Filled.KeyboardArrowDown,
    "KeyboardArrowLeft" to Icons.Filled.KeyboardArrowLeft,
    "KeyboardArrowRight" to Icons.Filled.KeyboardArrowRight,
    "KeyboardArrowUp" to Icons.Filled.KeyboardArrowUp,
    "List" to Icons.Filled.List,
    "LocationOn" to Icons.Filled.LocationOn,
    "Lock" to Icons.Filled.Lock,
    "MailOutline" to Icons.Filled.MailOutline,
    "Menu" to Icons.Filled.Menu,
    "MoreVert" to Icons.Filled.MoreVert,
    "Notifications" to Icons.Filled.Notifications,
    "Person" to Icons.Filled.Person,
    "Phone" to Icons.Filled.Phone,
    "Place" to Icons.Filled.Place,
    "PlayArrow" to Icons.Filled.PlayArrow,
    "Refresh" to Icons.Filled.Refresh,
    "Search" to Icons.Filled.Search,
    "Send" to Icons.Filled.Send,
    "Settings" to Icons.Filled.Settings,
    "Share" to Icons.Filled.Share,
    "ShoppingCart" to Icons.Filled.ShoppingCart,
    "Star" to Icons.Filled.Star,
    "ThumbUp" to Icons.Filled.ThumbUp,
    "Warning" to Icons.Filled.Warning,
)

private val materialSystemOutlinedIcons = mapOf(
    "AccountBox" to Icons.Outlined.AccountBox,
    "AccountCircle" to Icons.Outlined.AccountCircle,
    "Add" to Icons.Outlined.Add,
    "AddCircle" to Icons.Outlined.AddCircle,
    "ArrowBack" to Icons.Outlined.ArrowBack,
    "ArrowDropDown" to Icons.Outlined.ArrowDropDown,
    "ArrowForward" to Icons.Outlined.ArrowForward,
    "Build" to Icons.Outlined.Build,
    "Call" to Icons.Outlined.Call,
    "Check" to Icons.Outlined.Check,
    "CheckCircle" to Icons.Outlined.CheckCircle,
    "Clear" to Icons.Outlined.Clear,
    "Close" to Icons.Outlined.Close,
    "Create" to Icons.Outlined.Create,
    "DateRange" to Icons.Outlined.DateRange,
    "Delete" to Icons.Outlined.Delete,
    "Done" to Icons.Outlined.Done,
    "Edit" to Icons.Outlined.Edit,
    "Email" to Icons.Outlined.Email,
    "ExitToApp" to Icons.Outlined.ExitToApp,
    "Face" to Icons.Outlined.Face,
    "Favorite" to Icons.Outlined.Favorite,
    "FavoriteBorder" to Icons.Outlined.FavoriteBorder,
    "Home" to Icons.Outlined.Home,
    "Info" to Icons.Outlined.Info,
    "KeyboardArrowDown" to Icons.Outlined.KeyboardArrowDown,
    "KeyboardArrowLeft" to Icons.Outlined.KeyboardArrowLeft,
    "KeyboardArrowRight" to Icons.Outlined.KeyboardArrowRight,
    "KeyboardArrowUp" to Icons.Outlined.KeyboardArrowUp,
    "List" to Icons.Outlined.List,
    "LocationOn" to Icons.Outlined.LocationOn,
    "Lock" to Icons.Outlined.Lock,
    "MailOutline" to Icons.Outlined.MailOutline,
    "Menu" to Icons.Outlined.Menu,
    "MoreVert" to Icons.Outlined.MoreVert,
    "Notifications" to Icons.Outlined.Notifications,
    "Person" to Icons.Outlined.Person,
    "Phone" to Icons.Outlined.Phone,
    "Place" to Icons.Outlined.Place,
    "PlayArrow" to Icons.Outlined.PlayArrow,
    "Refresh" to Icons.Outlined.Refresh,
    "Search" to Icons.Outlined.Search,
    "Send" to Icons.Outlined.Send,
    "Settings" to Icons.Outlined.Settings,
    "Share" to Icons.Outlined.Share,
    "ShoppingCart" to Icons.Outlined.ShoppingCart,
    "Star" to Icons.Outlined.Star,
    "ThumbUp" to Icons.Outlined.ThumbUp,
    "Warning" to Icons.Outlined.Warning,
)

private val materialSystemRoundedIcons = mapOf(
    "AccountBox" to Icons.Rounded.AccountBox,
    "AccountCircle" to Icons.Rounded.AccountCircle,
    "Add" to Icons.Rounded.Add,
    "AddCircle" to Icons.Rounded.AddCircle,
    "ArrowBack" to Icons.Rounded.ArrowBack,
    "ArrowDropDown" to Icons.Rounded.ArrowDropDown,
    "ArrowForward" to Icons.Rounded.ArrowForward,
    "Build" to Icons.Rounded.Build,
    "Call" to Icons.Rounded.Call,
    "Check" to Icons.Rounded.Check,
    "CheckCircle" to Icons.Rounded.CheckCircle,
    "Clear" to Icons.Rounded.Clear,
    "Close" to Icons.Rounded.Close,
    "Create" to Icons.Rounded.Create,
    "DateRange" to Icons.Rounded.DateRange,
    "Delete" to Icons.Rounded.Delete,
    "Done" to Icons.Rounded.Done,
    "Edit" to Icons.Rounded.Edit,
    "Email" to Icons.Rounded.Email,
    "ExitToApp" to Icons.Rounded.ExitToApp,
    "Face" to Icons.Rounded.Face,
    "Favorite" to Icons.Rounded.Favorite,
    "FavoriteBorder" to Icons.Rounded.FavoriteBorder,
    "Home" to Icons.Rounded.Home,
    "Info" to Icons.Rounded.Info,
    "KeyboardArrowDown" to Icons.Rounded.KeyboardArrowDown,
    "KeyboardArrowLeft" to Icons.Rounded.KeyboardArrowLeft,
    "KeyboardArrowRight" to Icons.Rounded.KeyboardArrowRight,
    "KeyboardArrowUp" to Icons.Rounded.KeyboardArrowUp,
    "List" to Icons.Rounded.List,
    "LocationOn" to Icons.Rounded.LocationOn,
    "Lock" to Icons.Rounded.Lock,
    "MailOutline" to Icons.Rounded.MailOutline,
    "Menu" to Icons.Rounded.Menu,
    "MoreVert" to Icons.Rounded.MoreVert,
    "Notifications" to Icons.Rounded.Notifications,
    "Person" to Icons.Rounded.Person,
    "Phone" to Icons.Rounded.Phone,
    "Place" to Icons.Rounded.Place,
    "PlayArrow" to Icons.Rounded.PlayArrow,
    "Refresh" to Icons.Rounded.Refresh,
    "Search" to Icons.Rounded.Search,
    "Send" to Icons.Rounded.Send,
    "Settings" to Icons.Rounded.Settings,
    "Share" to Icons.Rounded.Share,
    "ShoppingCart" to Icons.Rounded.ShoppingCart,
    "Star" to Icons.Rounded.Star,
    "ThumbUp" to Icons.Rounded.ThumbUp,
    "Warning" to Icons.Rounded.Warning,
)

private val materialSystemSharpIcons = mapOf(
    "AccountBox" to Icons.Sharp.AccountBox,
    "AccountCircle" to Icons.Sharp.AccountCircle,
    "Add" to Icons.Sharp.Add,
    "AddCircle" to Icons.Sharp.AddCircle,
    "ArrowBack" to Icons.Sharp.ArrowBack,
    "ArrowDropDown" to Icons.Sharp.ArrowDropDown,
    "ArrowForward" to Icons.Sharp.ArrowForward,
    "Build" to Icons.Sharp.Build,
    "Call" to Icons.Sharp.Call,
    "Check" to Icons.Sharp.Check,
    "CheckCircle" to Icons.Sharp.CheckCircle,
    "Clear" to Icons.Sharp.Clear,
    "Close" to Icons.Sharp.Close,
    "Create" to Icons.Sharp.Create,
    "DateRange" to Icons.Sharp.DateRange,
    "Delete" to Icons.Sharp.Delete,
    "Done" to Icons.Sharp.Done,
    "Edit" to Icons.Sharp.Edit,
    "Email" to Icons.Sharp.Email,
    "ExitToApp" to Icons.Sharp.ExitToApp,
    "Face" to Icons.Sharp.Face,
    "Favorite" to Icons.Sharp.Favorite,
    "FavoriteBorder" to Icons.Sharp.FavoriteBorder,
    "Home" to Icons.Sharp.Home,
    "Info" to Icons.Sharp.Info,
    "KeyboardArrowDown" to Icons.Sharp.KeyboardArrowDown,
    "KeyboardArrowLeft" to Icons.Sharp.KeyboardArrowLeft,
    "KeyboardArrowRight" to Icons.Sharp.KeyboardArrowRight,
    "KeyboardArrowUp" to Icons.Sharp.KeyboardArrowUp,
    "List" to Icons.Sharp.List,
    "LocationOn" to Icons.Sharp.LocationOn,
    "Lock" to Icons.Sharp.Lock,
    "MailOutline" to Icons.Sharp.MailOutline,
    "Menu" to Icons.Sharp.Menu,
    "MoreVert" to Icons.Sharp.MoreVert,
    "Notifications" to Icons.Sharp.Notifications,
    "Person" to Icons.Sharp.Person,
    "Phone" to Icons.Sharp.Phone,
    "Place" to Icons.Sharp.Place,
    "PlayArrow" to Icons.Sharp.PlayArrow,
    "Refresh" to Icons.Sharp.Refresh,
    "Search" to Icons.Sharp.Search,
    "Send" to Icons.Sharp.Send,
    "Settings" to Icons.Sharp.Settings,
    "Share" to Icons.Sharp.Share,
    "ShoppingCart" to Icons.Sharp.ShoppingCart,
    "Star" to Icons.Sharp.Star,
    "ThumbUp" to Icons.Sharp.ThumbUp,
    "Warning" to Icons.Sharp.Warning,
)

private val materialSystemTwoToneIcons = mapOf(
    "AccountBox" to Icons.TwoTone.AccountBox,
    "AccountCircle" to Icons.TwoTone.AccountCircle,
    "Add" to Icons.TwoTone.Add,
    "AddCircle" to Icons.TwoTone.AddCircle,
    "ArrowBack" to Icons.TwoTone.ArrowBack,
    "ArrowDropDown" to Icons.TwoTone.ArrowDropDown,
    "ArrowForward" to Icons.TwoTone.ArrowForward,
    "Build" to Icons.TwoTone.Build,
    "Call" to Icons.TwoTone.Call,
    "Check" to Icons.TwoTone.Check,
    "CheckCircle" to Icons.TwoTone.CheckCircle,
    "Clear" to Icons.TwoTone.Clear,
    "Close" to Icons.TwoTone.Close,
    "Create" to Icons.TwoTone.Create,
    "DateRange" to Icons.TwoTone.DateRange,
    "Delete" to Icons.TwoTone.Delete,
    "Done" to Icons.TwoTone.Done,
    "Edit" to Icons.TwoTone.Edit,
    "Email" to Icons.TwoTone.Email,
    "ExitToApp" to Icons.TwoTone.ExitToApp,
    "Face" to Icons.TwoTone.Face,
    "Favorite" to Icons.TwoTone.Favorite,
    "FavoriteBorder" to Icons.TwoTone.FavoriteBorder,
    "Home" to Icons.TwoTone.Home,
    "Info" to Icons.TwoTone.Info,
    "KeyboardArrowDown" to Icons.TwoTone.KeyboardArrowDown,
    "KeyboardArrowLeft" to Icons.TwoTone.KeyboardArrowLeft,
    "KeyboardArrowRight" to Icons.TwoTone.KeyboardArrowRight,
    "KeyboardArrowUp" to Icons.TwoTone.KeyboardArrowUp,
    "List" to Icons.TwoTone.List,
    "LocationOn" to Icons.TwoTone.LocationOn,
    "Lock" to Icons.TwoTone.Lock,
    "MailOutline" to Icons.TwoTone.MailOutline,
    "Menu" to Icons.TwoTone.Menu,
    "MoreVert" to Icons.TwoTone.MoreVert,
    "Notifications" to Icons.TwoTone.Notifications,
    "Person" to Icons.TwoTone.Person,
    "Phone" to Icons.TwoTone.Phone,
    "Place" to Icons.TwoTone.Place,
    "PlayArrow" to Icons.TwoTone.PlayArrow,
    "Refresh" to Icons.TwoTone.Refresh,
    "Search" to Icons.TwoTone.Search,
    "Send" to Icons.TwoTone.Send,
    "Settings" to Icons.TwoTone.Settings,
    "Share" to Icons.TwoTone.Share,
    "ShoppingCart" to Icons.TwoTone.ShoppingCart,
    "Star" to Icons.TwoTone.Star,
    "ThumbUp" to Icons.TwoTone.ThumbUp,
    "Warning" to Icons.TwoTone.Warning,
)

private val materialSystemIcons = mutableMapOf<String, ImageVector>().let { umap ->
    materialSystemFilledIcons.mapKeysTo(umap) { entry ->
        "Filled." + entry.key
    }
    materialSystemOutlinedIcons.mapKeysTo(umap) { entry ->
        "Outlined." + entry.key
    }
    materialSystemRoundedIcons.mapKeysTo(umap) { entry ->
        "Rounded." + entry.key
    }
    materialSystemSharpIcons.mapKeysTo(umap) { entry ->
        "Sharp." + entry.key
    }
    materialSystemTwoToneIcons.mapKeysTo(umap) { entry ->
        "TwoTone." + entry.key
    }
    umap.toMap()
}


@Composable
fun IconByName(
    name: String,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
) {
    val icon = materialSystemIcons[name]
    if (icon != null) {
        Icon(icon, contentDescription, modifier, tint)
    }
}