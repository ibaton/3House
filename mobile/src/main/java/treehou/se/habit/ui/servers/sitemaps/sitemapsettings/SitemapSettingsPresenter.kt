package treehou.se.habit.ui.servers.sitemaps.sitemapsettings

import javax.inject.Inject

import treehou.se.habit.dagger.RxPresenter

class SitemapSettingsPresenter @Inject
constructor(private val view: SitemapSettingsContract.View) : RxPresenter(), SitemapSettingsContract.Presenter
