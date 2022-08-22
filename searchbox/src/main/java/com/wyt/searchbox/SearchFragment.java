package com.wyt.searchbox;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wyt.searchbox.adapter.SearchHistoryAdapter;
import com.wyt.searchbox.custom.CircularRevealAnim;
import com.wyt.searchbox.custom.IOnItemClickListener;
import com.wyt.searchbox.custom.IOnSearchClickListener;
import com.wyt.searchbox.db.SearchHistoryDB;
import com.wyt.searchbox.utils.KeyBoardUtils;

import java.util.ArrayList;

/**
 * Created by Won on 2017/1/13.
 */

public class SearchFragment extends DialogFragment implements DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener, CircularRevealAnim.AnimListener, IOnItemClickListener, View.OnClickListener {

    public static final String TAG = "SearchFragment";
    private ImageView ivSearchBack;
    private EditText etSearchKeyword;
    private ImageView ivSearchSearch;
    private RecyclerView rvSearchHistory;
    private View searchUnderline;
    private TextView tvSearchClean;
    private View viewSearchOutside;

    private View view;
    //动画
    private CircularRevealAnim mCircularRevealAnim;
    //历史搜索记录
    private ArrayList<String> allHistorys = new ArrayList<>();
    private ArrayList<String> historys = new ArrayList<>();
    //适配器
    private SearchHistoryAdapter searchHistoryAdapter;
    //数据库
    private SearchHistoryDB searchHistoryDB;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_search, container, false);

        init();//实例化

        return view;
    }

    private void init() {
        ivSearchBack = view.findViewById(R.id.iv_search_back);
        etSearchKeyword = view.findViewById(R.id.et_search_keyword);
        ivSearchSearch = view.findViewById(R.id.iv_search_search);
        rvSearchHistory = view.findViewById(R.id.rv_search_history);
        searchUnderline = view.findViewById(R.id.search_underline);
        tvSearchClean = view.findViewById(R.id.tv_search_clean);
        viewSearchOutside = view.findViewById(R.id.view_search_outside);

        //实例化动画效果
        mCircularRevealAnim = new CircularRevealAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(this);

        getDialog().setOnKeyListener(this);//键盘按键监听
        ivSearchSearch.getViewTreeObserver().addOnPreDrawListener(this);//绘制监听

        //实例化数据库
        searchHistoryDB = new SearchHistoryDB(getContext(), SearchHistoryDB.DB_NAME, null, 1);

        allHistorys = searchHistoryDB.queryAllHistory();
        setAllHistorys();
        //初始化recyclerView
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(getContext()));//list类型
        searchHistoryAdapter = new SearchHistoryAdapter(getContext(), historys);
        rvSearchHistory.setAdapter(searchHistoryAdapter);

        //设置删除单个记录的监听
        searchHistoryAdapter.setOnItemClickListener(this);
        //监听编辑框文字改变
        etSearchKeyword.addTextChangedListener(new TextWatcherImpl());
        //监听点击
        ivSearchBack.setOnClickListener(this);
        viewSearchOutside.setOnClickListener(this);
        ivSearchSearch.setOnClickListener(this);
        tvSearchClean.setOnClickListener(this);
        etSearchKeyword.requestFocus();

    }

    public void showFragment(FragmentManager fragmentManager, String tag) {
        if (!this.isAdded()) {
            this.show(fragmentManager, tag);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
            hideAnim();
        } else if (view.getId() == R.id.iv_search_search) {
            search();
        } else if (view.getId() == R.id.tv_search_clean) {
            searchHistoryDB.deleteAllHistory();
            historys.clear();
            searchUnderline.setVisibility(View.GONE);
            searchHistoryAdapter.notifyDataSetChanged();
        }
    }

    private void initDialog() {
        Window window = getDialog().getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消过渡动画 , 使DialogSearch的出现更加平滑
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            hideAnim();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            search();
        }
        return false;
    }


    @Override
    public boolean onPreDraw() {
        ivSearchSearch.getViewTreeObserver().removeOnPreDrawListener(this);
        mCircularRevealAnim.show(ivSearchSearch, view);
        return true;
    }


    @Override
    public void onHideAnimationEnd() {
        etSearchKeyword.setText("");
        dismiss();
    }


    @Override
    public void onShowAnimationEnd() {
        if (isVisible()) {
            KeyBoardUtils.openKeyboard(getContext(), etSearchKeyword);
        }
    }


    private class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String keyword = editable.toString();
            if (TextUtils.isEmpty(keyword.trim())) {
                setAllHistorys();
                searchHistoryAdapter.notifyDataSetChanged();
            } else {
                setKeyWordHistorys(editable.toString());
            }
        }
    }


    @Override
    public void onItemClick(String keyword) {
        iOnSearchClickListener.OnSearchClick(keyword);
        hideAnim();
    }

    @Override
    public void onItemDeleteClick(String keyword) {
        searchHistoryDB.deleteHistory(keyword);
        historys.remove(keyword);
        checkHistorySize();
        searchHistoryAdapter.notifyDataSetChanged();
    }

    private void hideAnim() {
        KeyBoardUtils.closeKeyboard(getContext(), etSearchKeyword);
        mCircularRevealAnim.hide(ivSearchSearch, view);
    }

    private void search() {
        String searchKey = etSearchKeyword.getText().toString();
        if (TextUtils.isEmpty(searchKey.trim())) {
            Toast.makeText(getContext(), "Enter keywords", Toast.LENGTH_SHORT).show();
        } else {
            iOnSearchClickListener.OnSearchClick(searchKey);
            searchHistoryDB.insertHistory(searchKey);
            hideAnim();
        }
    }

    private void checkHistorySize() {
        if (historys.size() < 1) {
            searchUnderline.setVisibility(View.GONE);
        } else {
            searchUnderline.setVisibility(View.VISIBLE);
        }
    }

    private void setAllHistorys() {
        historys.clear();
        historys.addAll(allHistorys);
        checkHistorySize();
    }

    private void setKeyWordHistorys(String keyword) {
        historys.clear();
        for (String string : allHistorys) {
            if (string.contains(keyword)) {
                historys.add(string);
            }
        }
        searchHistoryAdapter.notifyDataSetChanged();
        checkHistorySize();
    }

    private IOnSearchClickListener iOnSearchClickListener;

    public void setOnSearchClickListener(IOnSearchClickListener iOnSearchClickListener) {
        this.iOnSearchClickListener = iOnSearchClickListener;
    }

}
