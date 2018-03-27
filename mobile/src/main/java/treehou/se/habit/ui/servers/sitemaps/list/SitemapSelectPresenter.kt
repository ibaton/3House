package treehou.se.habit.ui.servers.sitemaps.list

import javax.inject.Inject

import treehou.se.habit.dagger.RxPresenter

class SitemapSelectPresenter @Inject
constructor(private val view: SitemapSelectContract.View) : RxPresenter(), SitemapSelectContract.Presenter
