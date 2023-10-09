package com.roadpass.icecreamroll.activity.homeparts;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.View;

import com.roadpass.icecreamroll.R;
import com.roadpass.icecreamroll.activity.HomeActivity;
import com.roadpass.icecreamroll.interfaces.DropTargetListener;
import com.roadpass.icecreamroll.manager.Setup;
import com.roadpass.icecreamroll.model.Item;
import com.roadpass.icecreamroll.util.Definitions;
import com.roadpass.icecreamroll.util.DragAction;
import com.roadpass.icecreamroll.util.DragAction.Action;
import com.roadpass.icecreamroll.util.Tool;
import com.roadpass.icecreamroll.widget.CellContainer;
import com.roadpass.icecreamroll.widget.Desktop;
import com.roadpass.icecreamroll.widget.ItemOptionView;

public class HpDragOption {
    public void initDragNDrop(@NonNull final HomeActivity _homeActivity, @NonNull final View leftDragHandle, @NonNull final View rightDragHandle, @NonNull final ItemOptionView dragNDropView) {
        final Handler dragHandler = new Handler();

        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getCurrentItem();
                    if (i > 0) {
                        _homeActivity.getDesktop().setCurrentItem(i - 1);
                    } else if (i == 0) {
                        _homeActivity.getDesktop().addPageLeft(true);
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return leftDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                leftDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {

            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {

            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0f);
            }
        });

        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getCurrentItem();
                    if (i < _homeActivity.getDesktop().getPages().size() - 1) {
                        _homeActivity.getDesktop().setCurrentItem(i + 1);
                    } else if (i == _homeActivity.getDesktop().getPages().size() - 1) {
                        _homeActivity.getDesktop().addPageRight(true);
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return rightDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                rightDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {

            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {

            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0f);
            }
        });


        dragNDropView.registerDropTarget(new DropTargetListener() {
            @Override
            public View getView() {
                return _homeActivity.getDesktop();
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                if (!DragAction.Action.SEARCH.equals(action))
                    _homeActivity.getItemOptionView().showItemPopup(_homeActivity);
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                _homeActivity.closeAppDrawer();
                if (Setup.appSettings().getDesktopShowGrid()) {
                    _homeActivity.getDock().setHideGrid(false);
                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
                        cellContainer.setHideGrid(false);
                    }
                }
            }

            @Override
            public void onEnter(Action action, PointF location) {

            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {

                if (DragAction.Action.DRAWER.equals(action)) {
                    if (_homeActivity.getAppDrawerController()._isOpen) {
                        return;
                    }
                    item.reset();
                }

                int x = (int) location.x;
                int y = (int) location.y;
                if (_homeActivity.getDesktop().addItemToPoint(item, x, y)) {
                    _homeActivity.getDesktop().consumeLastItem();
                    _homeActivity.getDock().consumeLastItem();

                    HomeActivity._db.saveItem(item, _homeActivity.getDesktop().getCurrentItem(), Definitions.ItemPosition.Desktop);
                } else {
                    Point pos = new Point();
                    _homeActivity.getDesktop().getCurrentPage().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
                    View itemView = _homeActivity.getDesktop().getCurrentPage().coordinateToChildView(pos);
                    if (itemView != null && Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDesktop().getCurrentPage(), _homeActivity.getDesktop().getCurrentItem(), Definitions.ItemPosition.Desktop, _homeActivity.getDesktop())) {
                        _homeActivity.getDesktop().consumeLastItem();
                        _homeActivity.getDock().consumeLastItem();
                    } else {
                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                        _homeActivity.getDesktop().revertLastItem();
                        _homeActivity.getDock().revertLastItem();
                    }
                }
            }

            @Override
            public void onMove(Action action, PointF location) {
                _homeActivity.getDesktop().updateIconProjection((int) location.x, (int) location.y);
            }

            @Override
            public void onExit(Action action, PointF location) {
                for (CellContainer page : _homeActivity.getDesktop().getPages()) {
                    page.clearCachedOutlineBitmap();
                }
                dragNDropView.cancelFolderPreview();
            }

            @Override
            public void onEnd() {
                for (CellContainer page : _homeActivity.getDesktop().getPages()) {
                    page.clearCachedOutlineBitmap();
                }
                if (Setup.appSettings().getDesktopShowGrid()) {
                    _homeActivity.getDock().setHideGrid(true);
                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
                        cellContainer.setHideGrid(true);
                    }
                }
            }
        });


        dragNDropView.registerDropTarget(new DropTargetListener() {
            @Override
            public View getView() {
                return _homeActivity.getDock();
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {

            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                if (DragAction.Action.DRAWER.equals(action)) {
                    if (_homeActivity.getAppDrawerController()._isOpen) {
                        return;
                    }
                    item.reset();
                }

                int x = (int) location.x;
                int y = (int) location.y;
                if (_homeActivity.getDock().addItemToPoint(item, x, y)) {
                    _homeActivity.getDesktop().consumeLastItem();
                    _homeActivity.getDock().consumeLastItem();


                    HomeActivity._db.saveItem(item, 0, Definitions.ItemPosition.Dock);
                } else {
                    Point pos = new Point();
                    _homeActivity.getDock().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
                    View itemView = _homeActivity.getDock().coordinateToChildView(pos);
                    if (itemView != null) {
                        if (Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDock(), 0, Definitions.ItemPosition.Dock, _homeActivity.getDock())) {
                            _homeActivity.getDesktop().consumeLastItem();
                            _homeActivity.getDock().consumeLastItem();
                        } else {
                            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                            _homeActivity.getDesktop().revertLastItem();
                            _homeActivity.getDock().revertLastItem();
                        }
                    } else {
                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                        _homeActivity.getDesktop().revertLastItem();
                        _homeActivity.getDock().revertLastItem();
                    }
                }
            }

            @Override
            public void onMove(Action action, PointF location) {
                _homeActivity.getDock().updateIconProjection((int) location.x, (int) location.y);
            }

            @Override
            public void onEnter(Action action, PointF location) {

            }

            @Override
            public void onExit(Action action, PointF location) {
                _homeActivity.getDock().clearCachedOutlineBitmap();
                dragNDropView.cancelFolderPreview();
            }

            @Override
            public void onEnd() {
                _homeActivity.getDock().clearCachedOutlineBitmap();
            }
        });
    }
}
