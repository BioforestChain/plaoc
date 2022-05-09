package org.bfchain.placo

//import androidx.compose.runtime.saveable.Saver /

//val NavigatorSaver = run {
//    val nidKey = "n"
//    val dataKey = "d"
//    val parentNidKey = "p"
//    val childNidsKey = "c"
//    mapSaver(
//        save = {
//            mapOf(
//                nidKey to it.nid,
//                dataKey to it.data,
//                parentNidKey to it.parentNid,
//                childNidsKey to it.childNids
//            )
//        },
//        restore = {
//            Navigator(
//                it[nidKey] as Int,
//                it[dataKey] as String,
//                it[parentNidKey] as Int,
//                it[childNidsKey] as MutableSet<Int>
//            )
//        }
//    )
//}
//val NavigatorMapSaver  =run {
//    val nidKey = "k"
//val navKey = "v"
//    mapSaver(
//        save = mapOf(nidKey to it.)
//    )
//}


val all_navigator_map = mutableMapOf<Int, Navigator>()
var activited_navigator: Navigator? = null

data class Navigator(
    val nid: Int = all_navigator_map.size,
    val data: String,
    val parentNid: Int = -1,
    var fromNid: Int = parentNid,
    val childNids: MutableSet<Int> = mutableSetOf(),
    var routeStackList: MutableList<String> = mutableListOf(),
) {
    init {
        all_navigator_map[nid] = this;
    }

    fun push(route: String): Boolean {
        if (routeStackList.last() == route) {
            return false
        }
        routeStackList.add(route)
        // @todo emit-push
        return true
    }

    fun pop(count: Int): Int {
        return routeStackList.size.let { size ->

            if (count >= size) {
                routeStackList.clear()
                size
            } else {
                routeStackList = routeStackList.subList(0, size - count).toMutableList()
                count
            }
        }.also {
            // @todo emit-pop
        }
    }

    fun replace(at: Int, route: String): Boolean {
        if (routeStackList.size <= at) {
            return false
        }
        routeStackList[at] = route
        // @todo emit-replace
        return true
    }

    fun fork(data: String): Int {
        val child = Navigator(data = data, parentNid = nid)
        childNids.add(child.nid)
        // @todo emit-fork
        return child.nid
    }

    fun checkout(targetNid: Int = nid): Boolean {
        if (childNids.contains(targetNid) || targetNid == nid) {
            activited_navigator = all_navigator_map[targetNid]!!.also { it ->
                it.fromNid = nid
            }
            // @todo emit-active
            // @todo emit-inactive
            return true
        }
        return false
    }


    fun destroy(targetNid: Int): Boolean {
        if (childNids.contains(targetNid) || targetNid == nid) {
            all_navigator_map[targetNid]!!.let { it ->
                it.destroySelf()
                // 如果销毁的是当前的导航，那么需要唤醒它的上一级
                if (activited_navigator == it) {
                    // 如果找不到它的上一级
                    all_navigator_map[it.fromNid].let { fromNav ->
                        fromNav?.checkout() ?: { activited_navigator = null }
                    }
                }
            }

            return true
        }
        return false
    }

    private fun destroySelf(fromParent: Boolean = false) {
        // 递归处理子集
        for (childNid in childNids) {
            all_navigator_map[childNid]!!.destroySelf(true)
        }
        // 删除自身
        all_navigator_map.remove(nid)
        // 如果不是父级调用的，那么需要主动向父级销毁自己的存在
        if (!fromParent) {
            all_navigator_map[parentNid]?.let { parent ->
                parent.childNids.remove(nid)
            }
        }
        // @todo emit-destroy
    }
}

/**
 * 创建一个 DWebview 的时候，如果声明了使用 dweb-navigator 插件，那么就注入这个 DWebNavigatorFFI 给它
 * 此时，会弃用 Web 原有的 History-API。
 *
 * 在 DWebview 环境中，`fork($dweb_app_id)` 会创建一个新的DWebview，这会发生在应用后端。
 * 并且该应用也要声明使用 dweb-navigator，否则 fork 只会返回创建失败（false）
 * 此时开发者可以对其进行 push 操作。
 * 最后通过 checkout 可以将其激活到应用前端。
 */
class DWebNavigatorFFI(nid: Int) {
    val nav = lazy { all_navigator_map[nid] ?: Navigator(nid, "") }
    fun init() {

    }
}