package com.roadpass.icecreamroll.widget;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Point;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.model.Item;
import com.roadpass.icecreamroll.model.Item.Type;
import com.roadpass.icecreamroll.util.Definitions;
import com.roadpass.icecreamroll.util.Definitions.ItemPosition;
import com.roadpass.icecreamroll.util.Definitions.ItemState;
import com.roadpass.icecreamroll.util.DragAction.Action;
import com.roadpass.icecreamroll.util.DragHandler;
import com.roadpass.icecreamroll.util.Tool;
import com.roadpass.icecreamroll.viewutil.DesktopCallback;
import com.roadpass.icecreamroll.viewutil.DesktopGestureListener;
import com.roadpass.icecreamroll.viewutil.ItemViewFactory;
import com.roadpass.icecreamroll.widget.CellContainer.DragState;

import java.util.ArrayList;
import java.util.List;

import in.championswimmer.sfg.lib.SimpleFingerGestures;
import in.championswimmer.sfg.lib.SimpleFingerGestures.OnFingerGestureListener;

import static com.roadpass.icecreamroll.util.Definitions.WallpaperScroll.Inverse;
import static com.roadpass.icecreamroll.util.Definitions.WallpaperScroll.Off;

public final class Desktop extends ViewPager implements DesktopCallback {
    private OnDesktopEditListener _desktopEditListener;
    public boolean _inEditMode;
    private PagerIndicator _pageIndicator;

    private final List<CellContainer> _pages = new ArrayList<>();
    private final Point _previousDragPoint = new Point();

    private Point _coordinate = new Point(-1, -1);
    private DesktopAdapter _adapter;
    private View _previousItemView;
    private int _previousPage;

    public Desktop(Context context) {
        super(context, null);

    }

    public Desktop(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public static boolean handleOnDropOver(HomeActivity homeActivity, Item dropItem, Item item, View itemView, CellContainer parent, int page, ItemPosition itemPosition, DesktopCallback callback) {
        if (item != null) {
            if (dropItem != null) {
                Type type = item._type;
                if (type != null) {
                    switch (type) {
                        case APP:
                        case SHORTCUT:
                            if (Type.APP.equals(dropItem._type) || Type.SHORTCUT.equals(dropItem._type)) {
                                parent.removeView(itemView);
                                Item group = Item.newGroupItem();
                                item._location = ItemPosition.Group;
                                dropItem._location = ItemPosition.Group;
                                group.getGroupItems().add(item);
                                group.getGroupItems().add(dropItem);
                                group._x = item._x;
                                group._y = item._y;
                                HomeActivity._db.saveItem(dropItem, page, ItemPosition.Group);
                                HomeActivity._db.saveItem(item, ItemState.Hidden);
                                HomeActivity._db.saveItem(dropItem, ItemState.Hidden);
                                HomeActivity._db.saveItem(group, page, itemPosition);
                                callback.addItemToPage(group, page);
                                HomeActivity launcher = HomeActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            } else if (Type.GROUP.equals(dropItem._type) && dropItem.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                parent.removeView(itemView);
                                Item group = Item.newGroupItem();
                                item._location = ItemPosition.Group;
                                dropItem._location = ItemPosition.Group;
                                group.getGroupItems().add(item);
                                group.getGroupItems().addAll(dropItem.getGroupItems());
                                group._x = item._x;
                                group._y = item._y;
                                HomeActivity._db.deleteItem(dropItem, false);
                                HomeActivity._db.saveItem(item, ItemState.Hidden);
                                HomeActivity._db.saveItem(group, page, itemPosition);
                                callback.addItemToPage(group, page);
                                HomeActivity launcher = HomeActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            }
                            break;
                        case GROUP:
                            if ((Item.Type.APP.equals(dropItem._type) || Type.SHORTCUT.equals(dropItem._type)) && item.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                parent.removeView(itemView);
                                dropItem._location = ItemPosition.Group;
                                item.getGroupItems().add(dropItem);
                                HomeActivity._db.saveItem(dropItem, page, ItemPosition.Group);
                                HomeActivity._db.saveItem(dropItem, ItemState.Hidden);
                                HomeActivity._db.saveItem(item, page, itemPosition);
                                callback.addItemToPage(item, page);
                                HomeActivity launcher = HomeActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            } else if (Type.GROUP.equals(dropItem._type) && item.getGroupItems().size() < GroupPopupView.GroupDef._maxItem && dropItem.getGroupItems().size() < GroupPopupView.GroupDef._maxItem) {
                                parent.removeView(itemView);
                                item.getGroupItems().addAll(dropItem.getGroupItems());
                                HomeActivity._db.saveItem(item, page, itemPosition);
                                HomeActivity._db.deleteItem(dropItem, false);
                                callback.addItemToPage(item, page);
                                HomeActivity launcher = HomeActivity.Companion.getLauncher();
                                if (launcher != null) {
                                    launcher.getDesktop().consumeLastItem();
                                    launcher.getDock().consumeLastItem();
                                }
                                return true;
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        }
        return false;
    }

    private float _startPosY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        detectSwipe(ev);
        super.dispatchTouchEvent(ev);
        return true;
    }


    private void detectSwipe(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                _startPosY = ev.getY();
                break;
            case 1:
                if (_startPosY - ev.getY() > 150.0f && Setup.appSettings().getGestureDockSwipeUp()) {
                    Point point = new Point((int) ev.getX(), (int) ev.getY());
                    if (!HomeActivity._launcher.getDesktop()._inEditMode) {
                        point = Tool.convertPoint(point, this, HomeActivity._launcher.getAppDrawerController());
                        if (Setup.appSettings().getGestureFeedback()) {
                            Tool.vibrate(this);
                        }
                        HomeActivity._launcher.openAppDrawer(this, point.x, point.y);
                    }
                    break;
                }
            default:
                break;
        }
    }

    public final class DesktopAdapter extends PagerAdapter {
        private final Desktop _desktop;

        public DesktopAdapter(Desktop desktop) {
            _desktop = desktop;
            _desktop.getPages().clear();
            int count = HomeActivity._db.getDesktop().size();
            if (count == 0) count++;
            for (int i = 0; i < count; i++) {
                _desktop.getPages().add(getItemLayout());
            }
        }

        private OnFingerGestureListener getGestureListener() {
            return new DesktopGestureListener(_desktop, Setup.desktopGestureCallback());
        }

        @SuppressLint("ClickableViewAccessibility")
        private CellContainer getItemLayout() {
            Context context = _desktop.getContext();
            CellContainer layout = new CellContainer(context);
            SimpleFingerGestures mySfg = new SimpleFingerGestures();
            mySfg.setOnFingerGestureListener(getGestureListener());
            layout.setGestures(mySfg);
            layout.setGridSize(Setup.appSettings().getDesktopColumnCount(), Setup.appSettings().getDesktopRowCount());
            layout.setOnClickListener(view -> exitDesktopEditMode());
            layout.setOnLongClickListener(v -> {
                if (!HomeActivity._launcher.getAppDrawerController()._isOpen) {
                    enterDesktopEditMode();
                    if (Setup.appSettings().getGestureFeedback()) {
                        Tool.vibrate(HomeActivity._launcher.getDesktop());
                    }
                }
                return true;
            });
            return layout;
        }

        public void addPageLeft() {
            _desktop.getPages().add(0, getItemLayout());
            notifyDataSetChanged();
        }

        public void addPageRight() {
            _desktop.getPages().add(getItemLayout());
            notifyDataSetChanged();
        }

        public void removePage(int position, boolean deleteItems) {
            if (deleteItems) {
                for (View view : _desktop.getPages().get(position).getAllCells()) {
                    Object item = view.getTag();
                    if (item instanceof Item) {
                        HomeActivity._db.deleteItem((Item) item, true);
                    }
                }
            }
            _desktop.getPages().remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return _desktop.getPages().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }

        @Override
        public boolean isViewFromObject(View p1, Object p2) {
            return p1 == p2;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            CellContainer layout = _desktop.getPages().get(position);
            container.addView(layout);
            return layout;
        }

        private void enterDesktopEditMode() {
            float scaleFactor = 0.8f;
            float translateFactor = (float) Tool.dp2px(Setup.appSettings().getSearchBarEnable() ? 20 : 40);
            for (CellContainer v : _desktop.getPages()) {
                v.setBlockTouch(true);
                v.animateBackgroundShow();
                ViewPropertyAnimator animation = v.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationY(translateFactor);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            _desktop.setInEditMode(true);
            if (_desktop.getDesktopEditListener() != null) {
                OnDesktopEditListener desktopEditListener = _desktop.getDesktopEditListener();
                desktopEditListener.onStartDesktopEdit();
            }
        }

        private void exitDesktopEditMode() {
            float scaleFactor = 1.0f;
            float translateFactor = 0.0f;
            for (CellContainer v : _desktop.getPages()) {
                v.setBlockTouch(false);
                v.animateBackgroundHide();
                ViewPropertyAnimator animation = v.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationY(translateFactor);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            _desktop.setInEditMode(false);
            if (_desktop.getDesktopEditListener() != null) {
                OnDesktopEditListener desktopEditListener = _desktop.getDesktopEditListener();
                desktopEditListener.onFinishDesktopEdit();
            }
        }
    }

    public final List<CellContainer> getPages() {
        return _pages;
    }

    public final OnDesktopEditListener getDesktopEditListener() {
        return _desktopEditListener;
    }

    public final void setDesktopEditListener(@Nullable OnDesktopEditListener v) {
        _desktopEditListener = v;
    }

    public final boolean getInEditMode() {
        return _inEditMode;
    }

    public final void setInEditMode(boolean v) {
        _inEditMode = v;
    }

    public final boolean isCurrentPageEmpty() {
        return getCurrentPage().getChildCount() == 0;
    }

    public final CellContainer getCurrentPage() {
        return _pages.get(getCurrentItem());
    }

    public final void setPageIndicator(PagerIndicator pageIndicator) {
        _pageIndicator = pageIndicator;
    }

    public final void initDesktop() {
        _adapter = new DesktopAdapter(this);
        setAdapter(_adapter);

        setCurrentItem(Setup.appSettings().getDesktopPageCurrent());
        if (Setup.appSettings().getDesktopShowIndicator() && _pageIndicator != null) {
            _pageIndicator.setViewPager(this);
        }
        int columns = Setup.appSettings().getDesktopColumnCount();
        int rows = Setup.appSettings().getDesktopRowCount();
        List<List<Item>> desktopItems = HomeActivity._db.getDesktop();
        for (int pageCount = 0; pageCount < desktopItems.size(); pageCount++) {
            List<Item> page = desktopItems.get(pageCount);
            _pages.get(pageCount).removeAllViews();
            for (int itemCount = 0; itemCount < page.size(); itemCount++) {
                Item item = page.get(itemCount);
                if (item._x + item._spanX <= columns && item._y + item._spanY <= rows) {
                    addItemToPage(item, pageCount);
                }
            }
        }
    }

    public final void addPageRight(boolean showGrid) {
        int previousPage = getCurrentItem();
        _adapter.addPageRight();
        setCurrentItem(previousPage + 1);
        if (Setup.appSettings().getDesktopShowGrid()) {
            for (CellContainer cellContainer : _pages) {
                cellContainer.setHideGrid(!showGrid);
            }
        }
        _pageIndicator.invalidate();
    }

    public final void addPageLeft(boolean showGrid) {
        int previousPage = getCurrentItem();
        _adapter.addPageLeft();
        setCurrentItem(previousPage + 1, false);
        setCurrentItem(previousPage - 1);
        if (Setup.appSettings().getDesktopShowGrid()) {
            for (CellContainer cellContainer : _pages) {
                cellContainer.setHideGrid(!showGrid);
            }
        }
        _pageIndicator.invalidate();
    }

    public final void removeCurrentPage() {
        int previousPage = getCurrentItem();
        _adapter.removePage(getCurrentItem(), true);
        if (_pages.size() == 0) {
            addPageRight(false);
            _adapter.exitDesktopEditMode();
        } else {
            setCurrentItem(previousPage, true);
            _pageIndicator.invalidate();
        }
    }

    public final void updateIconProjection(int x, int y) {
        HomeActivity launcher = HomeActivity.Companion.getLauncher();
        ItemOptionView dragNDropView = launcher.getItemOptionView();
        DragState state = getCurrentPage().peekItemAndSwap(x, y, _coordinate);
        if (!_coordinate.equals(_previousDragPoint)) {
            dragNDropView.cancelFolderPreview();
        }
        _previousDragPoint.set(_coordinate.x, _coordinate.y);
        switch (state) {
            case CurrentNotOccupied:
                getCurrentPage().projectImageOutlineAt(_coordinate, DragHandler._cachedDragBitmap);
                break;
            case CurrentOccupied:
                Item.Type type = dragNDropView.getDragItem()._type;
                for (CellContainer page : _pages) {
                    page.clearCachedOutlineBitmap();
                }
                if (!type.equals(Type.WIDGET) && (getCurrentPage().coordinateToChildView(_coordinate) instanceof AppItemView)) {
                    dragNDropView.showFolderPreviewAt(this, getCurrentPage().getCellWidth() * (_coordinate.x + 0.5f), getCurrentPage().getCellHeight() * (_coordinate.y + 0.5f));
                }
                break;
            case OutOffRange:
            case ItemViewNotFound:
            default:
                break;
        }
    }

    @Override
    public void setLastItem(Item item, View view) {
        _previousPage = getCurrentItem();
        _previousItemView = view;
        getCurrentPage().removeView(view);
    }

    @Override
    public void revertLastItem() {
        if (_previousItemView != null) {
            if (_adapter.getCount() >= _previousPage && _previousPage > -1) {
                CellContainer cellContainer = _pages.get(_previousPage);
                cellContainer.addViewToGrid(_previousItemView);
                _previousItemView = null;
                _previousPage = -1;
            }
        }
    }

    @Override
    public void consumeLastItem() {
        _previousItemView = null;
        _previousPage = -1;
    }

    public boolean addItemToPage(@NonNull Item item, int page) {
        View itemView = ItemViewFactory.getItemView(getContext(), this, Action.DESKTOP, item);
        if (itemView == null) {

            return false;
        }
        item._location = ItemPosition.Desktop;
        _pages.get(page).addViewToGrid(itemView, item._x, item._y, item._spanX, item._spanY);
        return true;
    }

    public boolean addItemToPoint(@NonNull Item item, int x, int y) {
        CellContainer.LayoutParams positionToLayoutPrams = getCurrentPage().coordinateToLayoutParams(x, y, item._spanX, item._spanY);
        if (positionToLayoutPrams == null) {
            return false;
        }
        item._location = ItemPosition.Desktop;
        item._x = positionToLayoutPrams.getX();
        item._y = positionToLayoutPrams.getY();
        View itemView = ItemViewFactory.getItemView(getContext(), this, Action.DESKTOP, item);
        if (itemView != null) {
            itemView.setLayoutParams(positionToLayoutPrams);
            getCurrentPage().addView(itemView);
        }
        return true;
    }

    public boolean addItemToCell(@NonNull Item item, int x, int y) {
        item._location = ItemPosition.Desktop;
        item._x = x;
        item._y = y;
        View itemView = ItemViewFactory.getItemView(getContext(), this, Action.DESKTOP, item);
        if (itemView == null) {
            return false;
        }
        getCurrentPage().addViewToGrid(itemView, item._x, item._y, item._spanX, item._spanY);
        return true;
    }

    public void removeItem(final View view, boolean animate) {
        if (animate) {
            view.animate().setDuration(100).scaleX(0.0f).scaleY(0.0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (getCurrentPage().equals(view.getParent())) {
                        getCurrentPage().removeView(view);
                    }
                }
            });
        } else if (getCurrentPage().equals(view.getParent())) {
            getCurrentPage().removeView(view);
        }
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        Definitions.WallpaperScroll scroll = Setup.appSettings().getDesktopWallpaperScroll();
        float xOffset = (position + offset) / (_pages.size() - 1);
        if (scroll.equals(Inverse)) {
            xOffset = 1f - xOffset;
        } else if (scroll.equals(Off)) {
            xOffset = 0.5f;
        }

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        wallpaperManager.setWallpaperOffsets(getWindowToken(), xOffset, 0.0f);
        super.onPageScrolled(position, offset, offsetPixels);
    }

    public interface OnDesktopEditListener {
        void onAdLoaded(MaxAd ad);

        void onAdDisplayed(MaxAd ad);

        void onAdHidden(MaxAd ad);

        void onAdClicked(MaxAd ad);

        void onAdLoadFailed(String adUnitId, MaxError error);

        void onAdDisplayFailed(MaxAd ad, MaxError error);

        void onStartDesktopEdit();

        void onFinishDesktopEdit();
    }

}
