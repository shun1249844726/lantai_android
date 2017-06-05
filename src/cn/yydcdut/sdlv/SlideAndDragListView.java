package cn.yydcdut.sdlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuyidong on 15/9/28.
 */
public class SlideAndDragListView<T> extends DragListView<T> implements WrapperAdapter.OnAdapterSlideListenerProxy,
        WrapperAdapter.OnAdapterMenuClickListenerProxy, WrapperAdapter.onItemDeleteListenerProxy,
        WrapperAdapter.OnScrollListenerProxy, AbsListView.OnItemLongClickListener {
    /* onTouch�����״̬ */
    private static final int STATE_NOTHING = -1;//̧��״̬
    private static final int STATE_DOWN = 0;//����״̬
    private static final int STATE_SCROLL = 2;//SCROLL״̬
    private static final int STATE_LONG_CLICK_FINISH = 3;//������Ѿ��������
    private static final int STATE_MORE_FINGERS = 4;//�����ָ
    private int mState = STATE_NOTHING;

    /* Scroll ��λ��ʱ����λ�� */
    private static final int RETURN_SCROLL_BACK_OWN = 1;//�Լ��й�λ����
    private static final int RETURN_SCROLL_BACK_OTHER = 2;//����λ���й�λ����
    private static final int RETURN_SCROLL_BACK_CLICK_MENU_BUTTON = 3;//������˻�����item��menuButton��
    private static final int RETURN_SCROLL_BACK_NOTHING = 0;//����λ�ö�û�лع����

    /* �Ƿ�Ҫ����itemClick */
    private boolean mIsWannaTriggerClick = true;
    /* �Ƿ��ڻ��� */
    private boolean mIsScrolling = false;
    /* �Ƿ����ڽ���delete�Ķ��� */
    private boolean mIsDeleteAnimationRunning = false;
    /* ��ָ���µ����� */
    private int mXDown;
    private int mYDown;
    /* Menu */
    private Map<Integer, Menu> mMenuMap;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;
    /* ��ָ��������̾��� */
    private int mShortestDistance = 25;
    /* CustomItemView������ߵľ��� */
    private int mItemLeftDistance = 0;
    /* ItemMainView�Ƿ����ڴ������Ʋ��� */
    private boolean isItemViewHandlingMotionEvent = false;

    /* ������ */
    private OnSlideListener mOnSlideListener;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnListItemLongClickListener mOnListItemLongClickListener;
    private OnListItemClickListener mOnListItemClickListener;
    private OnItemDeleteListener mOnItemDeleteListener;
    private OnListScrollListener mOnListScrollListener;

    public SlideAndDragListView(Context context) {
        this(context, null);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mShortestDistance = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
        //�ҵ��Ǹ�λ�õ�view
        View view = getChildAt(position - getFirstVisiblePosition());
        if (mOnListItemLongClickListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            if (itemMainLayout.getItemCustomView().getLeft() == 0) {
                mState = STATE_LONG_CLICK_FINISH;
                //�ع�
                mWrapperAdapter.returnSlideItemPosition();
                //�����ص�
                mOnListItemLongClickListener.onListItemLongClick(itemMainLayout.getItemCustomView(), position);
            }
        }
        if (mState == STATE_LONG_CLICK_FINISH || mState == STATE_DOWN) {
            startDrag(position);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //��Menu������Ȼ����Menu��λ�û������ǲ��ᾭ��onTouchEvent��ACTION_DOWN��
                //��ȡ��������
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //��ǰstate״̬Ϊ����
                mState = STATE_DOWN;
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition((int) ev.getX(), (int) ev.getY());
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerLeftAndRightMove(ev)) {//���·�Χ��50����Ҫ������һ���
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsDeleteAnimationRunning) {
            return false;
        }
        if (mIsScrolling) {
            return super.onTouchEvent(ev);
        }
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //��ȡ��������
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //��ǰstate״̬Ϊ����
                mState = STATE_DOWN;
                //�õ���ǰItem�����˶���
                ItemMainLayout itemMainLayoutDown = getItemMainLayoutByPosition(mXDown, mYDown);
                if (itemMainLayoutDown != null) {
                    mItemLeftDistance = itemMainLayoutDown.getItemCustomView().getLeft();
                } else {
                    mItemLeftDistance = 0;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mState = STATE_MORE_FINGERS;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (fingerLeftAndRightMove(ev) && !isItemViewHandlingMotionEvent) {//���·�Χ��50����Ҫ������һ���
                    int position = pointToPosition(mXDown, mYDown);
                    ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                    if (itemMainLayout != null) {
                        //�ж��ǲ��ǵ���menu������
                        if (mItemLeftDistance > 0) { //�Ѿ����һ����ˣ����һ�����
                            if (ev.getX() < mItemLeftDistance) {//��ָ��λ����Menu
                                return true;
                            }
                        } else if (mItemLeftDistance < 0) {//�Ѿ����󻬶��ˣ����һ�����
                            if (ev.getX() > mItemLeftDistance + itemMainLayout.getItemCustomView().getWidth()) {
                                return true;
                            }
                        }

                        //û�е���menu����
                        if (isFingerMoving2Right(ev)) {//��������һ���
                            if (itemMainLayout.getItemLeftBackGroundLayout().getBtnViews().size() == 0 &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//������û��Left��Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        } else if (isFingerMoving2Left(ev)) {//��������󻬶�
                            if (itemMainLayout.getItemRightBackGroundLayout().getBtnViews().size() == 0 &&
                                    itemMainLayout.getScrollState() == ItemMainLayout.SCROLL_STATE_CLOSE) {//������û��Right��Menu
                                mState = STATE_NOTHING;
                                return true;
                            }
                        }
                        //����ǰ��Ҫ������һ�����ݸ�wrapperAdapter
                        mWrapperAdapter.setSlideItemPosition(position);
                        isItemViewHandlingMotionEvent = true;
                        mState = STATE_SCROLL;
                        itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                        return true;
                    } else {
                        mState = STATE_NOTHING;
                        return true;
                    }
                } else {
                    if (isItemViewHandlingMotionEvent) {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, mItemLeftDistance);
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int position = pointToPosition(mXDown, mYDown);
                if (position != AdapterView.INVALID_POSITION) {
                    if (mState == STATE_DOWN || mState == STATE_LONG_CLICK_FINISH) {
                        //�Ƿ�ScrollBack�ˣ��ǵĻ��Ͳ�ȥִ��onListItemClick������
                        int scrollBackState = scrollBack(position, ev.getX());
                        if (scrollBackState == RETURN_SCROLL_BACK_NOTHING) {
                            if (mOnListItemClickListener != null && mIsWannaTriggerClick && !mIsScrolling) {
                                View v = getChildAt(position - getFirstVisiblePosition());
                                if (v instanceof ItemMainLayout) {
                                    ItemMainLayout itemMainLayout = (ItemMainLayout) v;
                                    mOnListItemClickListener.onListItemClick(itemMainLayout.getItemCustomView(), position);
                                }
                            }
                        }
                    } else {
                        ItemMainLayout itemMainLayout = getItemMainLayoutByPosition(mXDown, mYDown);
                        if (itemMainLayout != null) {
                            itemMainLayout.handleMotionEvent(ev, mXDown, mYDown, -1);
                        }
                    }
                }
                mState = STATE_NOTHING;
                mItemLeftDistance = 0;
                isItemViewHandlingMotionEvent = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                mState = STATE_NOTHING;
                mItemLeftDistance = 0;
                isItemViewHandlingMotionEvent = false;
                break;
            default:
                break;

        }
        return super.onTouchEvent(ev);
    }

    /**
     * ��������item��λ
     *
     * @param position
     * @param x        ����
     * @return
     */
    private int scrollBack(int position, float x) {
        //�ǲ��ǵ�ǰ���������
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            int scrollBackSituation = mWrapperAdapter.returnSlideItemPosition(x);
            switch (scrollBackSituation) {
                case ItemMainLayout.SCROLL_BACK_CLICK_OWN:
                    return RETURN_SCROLL_BACK_OWN;
                case ItemMainLayout.SCROLL_BACK_ALREADY_CLOSED:
                    return RETURN_SCROLL_BACK_NOTHING;
                case ItemMainLayout.SCROLL_BACK_CLICK_MENU_BUTTON:
                    return RETURN_SCROLL_BACK_CLICK_MENU_BUTTON;
            }
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return RETURN_SCROLL_BACK_OTHER;
        }
        return RETURN_SCROLL_BACK_NOTHING;
    }

    /**
     * ����drag��ScrollBack�߼�����
     *
     * @param position
     * @return true--->����drag false--->����drag
     */
    private boolean scrollBackByDrag(int position) {
        //�ǲ��ǵ�ǰ���������
        if (mWrapperAdapter.getSlideItemPosition() == position) {
            return false;
        } else if (mWrapperAdapter.getSlideItemPosition() != -1) {
            mWrapperAdapter.returnSlideItemPosition();
            return true;
        }
        return true;
    }

    /**
     * �������Ҳ��ܳ���50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < mShortestDistance && mXDown - ev.getX() > -mShortestDistance &&
                mYDown - ev.getY() < mShortestDistance && mYDown - ev.getY() > -mShortestDistance);
    }

    /**
     * ���ҵó���50�����²��ܳ���50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > mShortestDistance || ev.getX() - mXDown < -mShortestDistance) &&
                ev.getY() - mYDown < mShortestDistance && ev.getY() - mYDown > -mShortestDistance);
    }

    /**
     * �ǲ������һ���
     *
     * @return
     */
    private boolean isFingerMoving2Right(MotionEvent ev) {
        return (ev.getX() - mXDown > mShortestDistance);
    }

    /**
     * �ǲ������󻬶�
     *
     * @return
     */
    private boolean isFingerMoving2Left(MotionEvent ev) {
        return (ev.getX() - mXDown < -mShortestDistance);
    }

    /**
     * ͨ����ָ��XY����õ�ItemMainLayout
     *
     * @param x
     * @param y
     * @return
     */
    private ItemMainLayout getItemMainLayoutByPosition(int x, int y) {
        int position = pointToPosition(x, y);
        if (position != AdapterView.INVALID_POSITION) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof ItemMainLayout) {
                ItemMainLayout itemMainLayout = (ItemMainLayout) view;
                return itemMainLayout;
            }
        }
        return null;
    }

    /**
     * ����Menu
     *
     * @param menu
     */
    public void setMenu(Menu menu) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(1);
        }
        mMenuMap.put(menu.getMenuViewType(), menu);
    }

    /**
     * ����menu
     *
     * @param list
     */
    public void setMenu(List<Menu> list) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(list.size());
        }
        for (Menu menu : list) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * ����Menu
     *
     * @param menus
     */
    public void setMenu(Menu... menus) {
        if (mMenuMap != null) {
            mMenuMap.clear();
        } else {
            mMenuMap = new HashMap<>(menus.length);
        }
        for (Menu menu : menus) {
            mMenuMap.put(menu.getMenuViewType(), menu);
        }
    }

    /**
     * �رմ��˵�Item
     */
    public void closeSlidedItem() {
        mWrapperAdapter.returnSlideItemPosition();
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (mMenuMap == null || mMenuMap.size() == 0) {
            throw new IllegalArgumentException("������Menu");
        }
        mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter, mMenuMap);
        mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
        mWrapperAdapter.setOnAdapterMenuClickListenerProxy(this);
        mWrapperAdapter.setOnItemDeleteListenerProxy(this);
        mWrapperAdapter.setOnScrollListenerProxy(this);
        setRawAdapter(adapter);
        super.setAdapter(mWrapperAdapter);
    }

    @Override
    public void onScrollStateChangedProxy(AbsListView view, int scrollState) {
        if (scrollState == WrapperAdapter.SCROLL_STATE_IDLE) {
            mIsWannaTriggerClick = true;
            mIsScrolling = false;
        } else {
            mIsWannaTriggerClick = false;
            mIsScrolling = true;
        }
        if (mOnListScrollListener != null) {
            mOnListScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnListScrollListener != null) {
            mOnListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onDeleteBegin() {
        mIsDeleteAnimationRunning = true;

    }

    @Override
    public void onItemDelete(View view, int position) {
        mIsDeleteAnimationRunning = false;
        if (mOnItemDeleteListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnItemDeleteListener.onItemDelete(itemMainLayout.getItemCustomView(), position);
        }
    }

    /**
     * ���Drag
     *
     * @param position
     * @return
     */
    protected boolean startDrag(int position) {
        boolean canDrag = scrollBackByDrag(position);
        //�ҵ��Ǹ�λ�õ�view
        View view = getChildAt(position - getFirstVisiblePosition());
        if (canDrag && view instanceof ItemMainLayout) {
            setDragPosition(position, mWrapperAdapter.isWannaTransparentWhileDragging(position));
        }
        return canDrag && view instanceof ItemMainLayout;
    }

    /**
     * ����item����������
     *
     * @param listener
     */
    public void setOnSlideListener(OnSlideListener listener) {
        mOnSlideListener = listener;
    }

    /**
     * item�Ļ����ļ�����
     */
    public interface OnSlideListener {
        /**
         * ����������ʱ�򴥷�
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideOpen(View view, View parentView, int position, int direction);

        /**
         * ��������λ��ʱ�򴥷�
         *
         * @param view
         * @param parentView
         * @param position
         */
        void onSlideClose(View view, View parentView, int position, int direction);
    }

    @Override
    public void onSlideOpen(View view, int position, int direction) {
        if (mOnSlideListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnSlideListener.onSlideOpen(itemMainLayout.getItemCustomView(), this, position, direction);
        }
    }

    @Override
    public void onSlideClose(View view, int position, int direction) {
        if (mOnSlideListener != null && view instanceof ItemMainLayout) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) view;
            mOnSlideListener.onSlideClose(itemMainLayout.getItemCustomView(), this, position, direction);
        }
    }

    /**
     * ����item�е�button����¼��ļ�����
     *
     * @param onMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    /**
     * item�е�button������
     */
    public interface OnMenuItemClickListener {
        /**
         * ����¼�
         *
         * @param v
         * @param itemPosition   �ڼ���item
         * @param buttonPosition �ڼ���button
         * @param direction      ����
         * @return �ο�Menu�ļ�������
         */
        int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        if (mOnMenuItemClickListener != null) {
            return mOnMenuItemClickListener.onMenuItemClick(v, itemPosition, buttonPosition, direction);
        }
        return Menu.ITEM_NOTHING;
    }

    @Deprecated
    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
    }

    /**
     * ���ü�����
     *
     * @param listener
     */
    public void setOnListItemClickListener(OnListItemClickListener listener) {
        mOnListItemClickListener = listener;
    }

    /**
     * �Լ��ĵ����¼�
     */
    public interface OnListItemClickListener {
        void onListItemClick(View v, int position);
    }

    /**
     * {@link #setOnListItemLongClickListener(OnListItemLongClickListener)}
     *
     * @param listener
     */
    @Deprecated
    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
    }

    /**
     * ���ü�����
     *
     * @param listener
     */
    public void setOnListItemLongClickListener(OnListItemLongClickListener listener) {
        mOnListItemLongClickListener = listener;
        super.setOnItemLongClickListener(this);
    }

    /**
     * �Լ�д�ĳ�����¼�
     */
    public interface OnListItemLongClickListener {
        void onListItemLongClick(View view, int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener) {
        mOnItemDeleteListener = onItemDeleteListener;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(View view, int position);
    }

    @Deprecated
    @Override
    public void setOnScrollListener(OnScrollListener l) {
    }

    public void setOnListScrollListener(OnListScrollListener onListScrollListener) {
        mOnListScrollListener = onListScrollListener;
    }

    /**
     * �Լ��õ���
     *
     * @param l
     */
    protected void setOnSuperScrollListener(OnScrollListener l) {
        super.setOnScrollListener(l);
    }

    public interface OnListScrollListener {
        int SCROLL_STATE_IDLE = 0;
        int SCROLL_STATE_TOUCH_SCROLL = 1;
        int SCROLL_STATE_FLING = 2;

        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
