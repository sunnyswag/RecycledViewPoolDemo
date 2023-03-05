# RecycledViewPool 使用场景分析

RecycledViewPool 适合怎样的使用场景呢？这是这篇文章，我们需要讨论的问题。具体的代码实现可以在 [Github仓库](https://github.com/sunnyswag/RecycledViewPoolDemo) 查看到。

同时，在阅读这篇文章之前，需要简单了解一下 RecyclerView 的缓存结构(可参考本文 REFERENCE 的第三篇)。Scraped 缓存是界面当前显示的内容。滑出屏幕之后会将 Item 暂存到 Cached 缓存，方便下次滑回来时再次使用，此时的数据是干净的，不需要走 onBindViewHolder。而超出 Cached 缓存的区域会根据 viewType 归类，放到 RecycledViewPool 中，从 RecycledViewPool 拿出来的数据是需要调用 onBindViewHolder 重新加载的。



### 构建测试环境

为了构建测试环境，我们将会实现一个 Viewpager -> Fragment -> Recyclerview 的 UI 结构，多个 Recyclerview 中的 item 取自同一个 RecycledViewPool，从而实现 item 的复用。具体的结构可以参考下图：

<img src="https://tern-1257001564.cos.ap-guangzhou.myqcloud.com/markdown_pic/recycledViewPoolSketch.png" alt="recycledViewPoolSketch" style="zoom:60%;" />



### 书写测试代码

#### 构建供测试的代码框架(commitId: 046541a4)

我们将会构建一个  Viewpager -> Fragment -> Recyclerview 的 UI 结构。在当前情况下，每个 Recyclerview 都会使用各自的 RecycledViewPool，并且各自的 RecycledViewPool 互不干扰。简要的代码逻辑如下：

**ViewPagerAdapter：**

```kotlin
class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    // ViewPagerAdapter 的其他逻辑...
    
    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    companion object {
        private const val ITEM_COUNT = 5
    }
}
```

**ViewPagerItemFragment：**

```kotlin
class ViewPagerItemFragment: Fragment() {

    // Fragment 的其他逻辑...

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRoot.apply {
            adapter = RecyclerViewAdapter()
            layoutManager = LinearLayoutManager(context)
            // 将 recyclerView 的 cacheSize 设置为 0，
            // 让不在屏幕的 item 直接进入到 RecycledViewPool 中
            setItemViewCacheSize(0)
        }
    }
}
```

**RecyclerViewAdapter：**

* 当调用 onCreateViewHolder 或者 onBindViewHolder 时，会将相应的 TextView 显示出来，而在调用 onViewDetachedFromWindow 会将其隐藏掉。可以非常直观地查看到该 item 在显示过程中，调用了哪个方法。
* 将 ITEM_COUNT 设置为 20，方便观察现象。

```kotlin
class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.tvOnCreate.apply {
            // 调用 onCreateViewHolder 时，将相应的 TextView 显示出来
            text = "onCreateViewHolder invoke, viewType: $viewType"
            visibility = View.VISIBLE
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvOnBind.apply {
            // 调用 onBindViewHolder时，将相应的 TextView 显示出来
            text = "onBindViewHolder invoke, position: $position"
            visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        // 在 onViewDetachedFromWindow 时将两个 TextView 都隐藏掉
        holder.binding.tvOnBind.visibility = View.INVISIBLE
        holder.binding.tvOnCreate.visibility = View.INVISIBLE
    }

    class ViewHolder(val binding: RecyclerviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TAG = "RecyclerViewAdapter"
        const val ITEM_COUNT = 20
    }
}
```

**测试结果分析**

<img src="https://tern-1257001564.cos.ap-guangzhou.myqcloud.com/markdown_pic/no_rvpool.gif" alt="no_rvpool" style="zoom:67%;" />

* 在单个 RecyclerView 中滑动时，后面的 item 不会调用 onCreateViewHolder，会直接调用 onBindViewHolder。因为在滑动过程中，会有 item 被放入到 RecycledViewPool，而后面的 Item 是从 RecycledViewPool 中取出来的，所以只需调用 onBindViewHolder 即可进行加载。
* 但是，滑动到另外一个 RecyclerView 时，所有的 Item 都需要重新构建。接下来，我们考虑使用 RecycledViewPool 来实现不同页面滑动加载的优化。



#### 引入 RecycledViewPool (commitId: d1c822ba)

**RecyclerViewItemViewModel：**

我们把 RecycledViewPool 放到 ViewModel 中，该 ViewModel 会在 Viewpager 层进行共享，方便 Viewpager 的 Fragment 获取到 RecycledViewPool 实例。

```kotlin
class RecyclerViewItemViewModel: ViewModel() {

    var viewPool = RecycledViewPoolTest().apply {
        // 设置 viewType 为 0 的 pool 大小为 10
        setMaxRecycledViews(0, 10)
    }
        private set

    // RecyclerViewItemViewModel 的其他逻辑...
}
```

**ViewPagerItemFragment：**

```kotlin
class ViewPagerItemFragment: Fragment() {

    // Fragment 的其他逻辑...
    
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(RecyclerViewItemViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 将 viewModel 中的 viewPool 塞给每个 RecyclerView 
        binding.rvRoot.setRecycledViewPool(viewModel.viewPool)
        
        // onViewCreated 的其他逻辑...
    }
}
```

**测试结果分析：**

<img src="https://tern-1257001564.cos.ap-guangzhou.myqcloud.com/markdown_pic/with_rv_pool.gif" alt="with_rv_pool" style="zoom:60%;" />

* 在单个 RecyclerView 中滑动时，和没有共享 RecycledViewPool 的表现一致。
* 滑动到另一个 RecyclerView 时，会有两个 item 的复用，这里为啥只有两个 item 的复用呢?，RecycledViewPool  我们设置的大小为 10，就算是默认的大小也是 5，怎么也不应该只有两个 item 的复用。
* 这是因为每个 item 的 viewType 相同，在单个  RecyclerView 中滑动时，会不断有 item 放到 RecyclerViewPool 中。同时，也不断有 item 从RecyclerViewPool 中取出来。所以实际上在切换 viewPager 的时候 RecyclerViewPool 里面只会存在 2 个缓存的 ViewHolder。
* 但实际上，多个 RecyclerView 使用同一个 RecyclerViewPool，这样可以减少内存的消耗。那还有优化的空间吗？



#### recycledViewPool + 多个 viewType (commitId: 80776521)

**RecyclerViewAdapter：**

我们修改 RecyclerViewAdapter 让其支持多个 viewType。具体的逻辑为：第 0~19 个 item 使用的是 BlueViewHolder，第 20~39 个 item 使用的是 PurpleViewHolder。

```kotlin
class RecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // onCreateViewHolder 的其他逻辑...
        
        return if (viewType == VIEW_TYPE_BLUE) {
            BlueViewHolder(binding)
        } else {
            PurpleViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return ITEM_COUNT
    }

    override fun getItemViewType(position: Int): Int {
        val middle = ITEM_COUNT / 2
        return if (position <= middle) {
            VIEW_TYPE_BLUE
        } else {
            VIEW_TYPE_PURPLE
        }
    }
    
    companion object {
        const val ITEM_COUNT = 40

        const val VIEW_TYPE_BLUE = 0
        const val VIEW_TYPE_PURPLE = 1
    }
    
    // RecyclerViewAdapter 的其他逻辑...
}
```

**测试结果分析：**

<img src="https://tern-1257001564.cos.ap-guangzhou.myqcloud.com/markdown_pic/rvpool_with_multi_type.gif" alt="rvpool_with_multi_type" style="zoom:60%;" />

* 在单个 RecyclerView 中滑动时，多个 item 各自的表现和之前一致。
* 滑动到另一个 RecyclerView 时，我们会发现完全没有了 onCreateViewHolder 的逻辑，非常的神奇。其实，这是因为在滑动到另外一个 RecyclerView 之前，recycledViewPool 中已经存满了蓝色 viewType 的 item。所以当滑动到下一个 RecyclerView 时，只需要取其中的 item 并调用 onBindViewHolder 即可。
* 可见，RecycledViewPool 在存在多个 viewType 的场景，可以发挥最大的效益。



#### 总结及注意事项

* recycledViewPool 在多个 viewType 的场景下使用会更佳。当然在只有一个 viewType 的场景下使用也不错，可以减少内存的消耗。
* 需要注意内存泄漏问题：在 ViewModel onCleared() 时，记得调用 recycledViewPool 的 clear() 方法清理 viewPool。以及在 Fragment 销毁时，也记得将 recycledViewPool clear 掉。



## Reference

[PagerActivity.java](https://gist.github.com/yrom/728237025575005a8fd3)

[RecycledViewPool使用](https://www.jianshu.com/p/122e68e9ddac)

[画了10张图，带你搞定RecyclerView的缓存复用机制](https://mp.weixin.qq.com/s/ymdkjE8AFiYhiyj7Av2aVg)
