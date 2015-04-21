package treehou.se.habit.ui.widgets;

import treehou.se.habit.core.LinkedPage;

/**
 * Created by ibaton on 2014-10-19.
 *
 * Builds widgets used to represent an item in the openhab sitemap.
 */
public interface IPageCallback {

    public void pageSelected(LinkedPage linkedPage);
}
