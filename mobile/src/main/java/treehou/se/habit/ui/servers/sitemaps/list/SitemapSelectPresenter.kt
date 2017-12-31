package treehou.se.habit.ui.servers.sitemaps.list

import javax.inject.Inject

import treehou.se.habit.module.RxPresenter
import treehou.se.habit.ui.servers.serverlist.ServersContract

class SitemapSelectPresenter @Inject
constructor(private val view: SitemapSelectContract.View) : RxPresenter(), SitemapSelectContract.Presenter
