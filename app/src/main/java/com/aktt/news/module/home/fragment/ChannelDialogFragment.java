package com.aktt.news.module.home.fragment;

import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import butterknife.BindView;
import butterknife.OnClick;
import com.aktt.news.R;
import com.aktt.news.base.BaseDialogFragment2;
import com.aktt.news.data.Channel;
import com.aktt.news.instance.TabManager;
import com.aktt.news.module.home.adapter.ChannelAdapter;
import com.aktt.news.module.home.callback.ItemDragHelperCallBack;
import com.aktt.news.module.home.callback.OnChannelDragListener;
import com.aktt.news.module.home.callback.OnChannelListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.util.ArrayList;
import java.util.List;

import static com.aktt.news.data.Channel.TYPE_MY_CHANNEL;

/**
 * Created by magical on 2017/8/18 .
 * 频道编辑的 fragment
 */

public class ChannelDialogFragment extends BaseDialogFragment2 implements OnChannelDragListener {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;

    private ChannelAdapter mAdapter;
    private ItemTouchHelper mHelper;
    private List<Channel> mDatas = new ArrayList<>();

    private OnChannelListener mOnChannelListener;
    private boolean hasChanged;     //判断是否有操作变化，否则不更新

    public void setOnChannelListener(OnChannelListener onChannelListener) {
        mOnChannelListener = onChannelListener;
    }

    public static ChannelDialogFragment newInstance() {
        ChannelDialogFragment dialogFragment = new ChannelDialogFragment();
        return dialogFragment;
    }

    //@Override
    //public void onCreate(@Nullable Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
    //}

    //@Nullable
    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //        Bundle savedInstanceState) {
    //
    //    final Window window = getDialog().getWindow();
    //    View view = inflater.inflate(fragment_home_channel_edit,
    //            ((ViewGroup) window.findViewById(android.R.id.content)),
    //            false);//需要用android.R.id.content这个view
    //    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
    //    window.setLayout(-1, -1);//这2行,和上面的一样,注意顺序就行;
    //    return view;
    //}

    //@Override
    //public void onViewCreated(View view, Bundle savedInstanceState) {
    //    super.onViewCreated(view, savedInstanceState);
    //    ButterKnife.bind(this, view);
    //    processLogic();
    //}

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_home_channel_edit;
    }

    @Override
    protected void initView() {
        super.initView();
        processLogic();
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.statusBarDarkFont(true).init();
    }

    private void processLogic() {

        // my channel title
        mDatas.add(new Channel(Channel.TYPE_MY, "我的频道", ""));

        // my channel list
        List<Channel> myList = TabManager.getInstance().getTabList(getActivity());
        mDatas.addAll(myList);

        // other channel title
        mDatas.add(new Channel(Channel.TYPE_OTHER, "频道推荐", ""));

        // other channel list
        List<Channel> otherList = TabManager.getInstance().getOtherList(getActivity());
        mDatas.addAll(otherList);

        mAdapter = new ChannelAdapter(mDatas);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //ToastUtils.showShort(String.valueOf(position));
            }
        });
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 4);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int itemViewType = mAdapter.getItemViewType(position);
                return itemViewType == TYPE_MY_CHANNEL || itemViewType == Channel.TYPE_OTHER_CHANNEL
                        ? 1 : 4;
            }
        });
        ItemDragHelperCallBack callBack = new ItemDragHelperCallBack(this);
        mHelper = new ItemTouchHelper(callBack);
        mAdapter.setOnChannelDragListener(this);
        //attach to RecyclerView
        mHelper.attachToRecyclerView(mRecyclerView);
    }

    @OnClick(R.id.icon_collapse)
    public void onClick(View v) {
        dismiss();
    }

    private DialogInterface.OnDismissListener mOnDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null && hasChanged) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onStarDrag(BaseViewHolder baseViewHolder) {
        //开始拖动
        mHelper.startDrag(baseViewHolder);
    }

    @Override
    public void onItemMove(int starPos, int endPos) {
        //        if (starPos < 0||endPos<0) return;
        //我的频道之间移动
        if (mOnChannelListener != null) {
            mOnChannelListener.onItemMove(starPos - 1, endPos - 1);//去除标题所占的一个index
        }
        onMove(starPos, endPos);
    }

    private void onMove(int starPos, int endPos) {

        //添加、删除、换位都会调到这里 所以我们在此改变标记
        hasChanged = true;

        Channel startChannel = mDatas.get(starPos);
        //先删除之前的位置
        mDatas.remove(starPos);
        //添加到现在的位置
        mDatas.add(endPos, startChannel);
        mAdapter.notifyItemMoved(starPos, endPos);
    }

    @Override
    public void onMoveToMyChannel(int starPos, int endPos) {
        //移动到我的频道
        onMove(starPos, endPos);

        if (mOnChannelListener != null) {
            mOnChannelListener.onMoveToMyChannel(starPos - 1 - mAdapter.getMyChannelSize(),
                    endPos - 1);
        }
    }

    @Override
    public void onMoveToOtherChannel(int starPos, int endPos) {
        //移动到推荐频道
        onMove(starPos, endPos);
        if (mOnChannelListener != null) {
            mOnChannelListener.onMoveToOtherChannel(starPos - 1,
                    endPos - 2 - mAdapter.getMyChannelSize());
        }
    }
}
