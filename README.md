# RecycledViewPoolDemo
RecycledViewPool 使用时的 Demo(在不考虑 Adapter 缓存的情况下)

a. RecyclerView 无 recycledViewPool 的实现 `046541a4`：此时每个 RecyclerView 使用各自的 RecyclerViewPool 互相不影响

b. RecyclerView, recycledViewPool 的实现 `d1c822ba`：每个 RecyclerView 使用同一个 RecyclerViewPool，但是由于每个 item 的 viewType 相同，所以实际上在切换 viewPager 的时候 RecyclerViewPool 里面只会存在 2 个缓存的 ViewHolder，实际效果不太理想

c. RecyclerView, recycledViewPool 的多个 viewType 的实现 `80776521`：在有多个 viewType 的情况下，在同一个 RecyclerView 中滑动时，会把另一个 viewType 的 ViewHolder 放到 recycledViewPool 中，加载下个 Fragment 时，会把缓存的 ViewHolder 拿出来使用。在多个 viewType 的情况下，recycledViewPool 实现的效果更好



注意内存泄漏问题



## Reference

https://gist.github.com/yrom/728237025575005a8fd3

https://www.jianshu.com/p/122e68e9ddac
