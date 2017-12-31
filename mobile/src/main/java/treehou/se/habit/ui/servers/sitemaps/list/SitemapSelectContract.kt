package treehou.se.habit.ui.servers.sitemaps.list

import treehou.se.habit.mvp.BasePresenter
import treehou.se.habit.mvp.BaseView

interface SitemapSelectContract {

    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}
