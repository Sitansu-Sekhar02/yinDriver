package com.yin.driver.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vlonjatg.progressactivity.ProgressLinearLayout;
import com.yin.driver.AppController;
import com.yin.driver.MainActivity;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.services.ServerResponseInterface;
import com.yin.driver.services.ServicesMethodsManager;
import com.yin.driver.services.model.DriverTaskListListModel;
import com.yin.driver.services.model.DriverTaskModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {
    public static final String TAG = "HomeFragment";

    public static final String BUNDEL_HOME_EVENTS = "Home";
    Activity activity;
    Context context;
    ProgressLinearLayout progressActivity;
    GlobalFunctions globalFunctions;
    GlobalVariables globalVariables;
    RecyclerView home_list_rv;
    View mainView;
    HomeListAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    List <DriverTaskModel> list = new ArrayList <>();
    SwipeRefreshLayout swipe_container;
    DriverTaskModel driverTaskModel;
    CircleImageView driver_iv;
    TextView driver_name_tv, location_tv, customer_request_tv;
    LinearLayout main_ll;

    private boolean shouldRefreshOnResume = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState );
        View view = inflater.inflate( R.layout.fragment_home_main, container, false );
        activity = getActivity();
        context = getActivity();
        setHasOptionsMenu( true );

        globalFunctions = AppController.getInstance().getGlobalFunctions();
        globalVariables = AppController.getInstance().getGlobalVariables();
        linearLayoutManager = new LinearLayoutManager( activity );
        progressActivity = view.findViewById( R.id.details_progressActivity );
        home_list_rv = view.findViewById( R.id.fragment_home_list_rv );
        swipe_container = view.findViewById( R.id.swipe_container );

        mainView = home_list_rv;
        loadList( context );

        driver_iv = view.findViewById( R.id.row_list_news_list_driver_iv );
        driver_name_tv = view.findViewById( R.id.row_list_news_list_driver_name_tv );
        location_tv = view.findViewById( R.id.row_list_location_tv );
        customer_request_tv = view.findViewById( R.id.row_list_customer_request_tv );

        main_ll = view.findViewById( R.id.main_ll );

     /*   swipe_container.setOnRefreshListener( this );
        swipe_container.setColorSchemeResources( R.color.fab_color_normal,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark );*/

        swipe_container.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadList( context );
            }
        } );

        return view;
    }

    /*  @Override
      public void setUserVisibleHint(boolean isVisibleToUser) {
          super.setUserVisibleHint( isVisibleToUser );
          if (isVisibleToUser) {
  //            getFragmentManager().beginTransaction().detach( this ).attach( this ).commit();
              loadList( context );
          }
      }
  */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint( isVisibleToUser );
        // Refresh tab data:

        if (getFragmentManager() != null) {

            getFragmentManager()
                    .beginTransaction()
                    .detach( this )
                    .attach( this )
                    .commit();
        }
    }

    @Override
    public void onResume() {
        (( MainActivity ) activity).setTitle( getString( R.string.app_name ), R.drawable.ic_app_menu, 0 );
        // ((MainActivity) activity).setTitle("", 0, 0);
        super.onResume();
        if (shouldRefreshOnResume) {
            loadList( context );
        }
     /*   if (getFragmentManager().findFragmentByTag( TAG ) != null)
            getFragmentManager().findFragmentByTag( TAG ).getRetainInstance();*/
    }

    private void loadList(final Context context) {

        GlobalFunctions.showProgress( context, getString( R.string.loading ) );
        ServicesMethodsManager servicesMethodsManager = new ServicesMethodsManager();
        servicesMethodsManager.getDriverTaskList( context, new ServerResponseInterface() {
            @Override
            public void OnSuccessFromServer(Object arg0) {
                GlobalFunctions.hideProgress();
                if (swipe_container.isRefreshing()) {
                    swipe_container.setRefreshing( false );
                }
                Log.d( TAG, "Response : " + arg0.toString() );
                DriverTaskListListModel model = ( DriverTaskListListModel ) arg0;
                setThisPage( model );
                setDriverView( model );
            }

            @Override
            public void OnFailureFromServer(String msg) {
                GlobalFunctions.hideProgress();
                if (swipe_container.isRefreshing()) {
                    swipe_container.setRefreshing( false );
                }
                GlobalFunctions.displayMessaage( context, mainView, msg );
            }

            @Override
            public void OnError(String msg) {
                GlobalFunctions.hideProgress();
                if (swipe_container.isRefreshing()) {
                    swipe_container.setRefreshing( false );
                }
                GlobalFunctions.displayMessaage( context, mainView, msg );
            }
        }, "LeaderBoardList" );
    }

    private void setDriverView(DriverTaskListListModel model) {

        if (model != null) {
            if (model.getDriverTaskModels() != null) {
                if (model.getDriverTaskModels().size() > 0) {
                    driverTaskModel = model.getDriverTaskModels().get( 0 );
                    setDriverDetails( driverTaskModel );
                }
            } /*else if (list.size() <= 0) {
//                showEmptyPage();
                main_ll.setVisibility( View.GONE );
            }*/
        }

    }

    private void setDriverDetails(DriverTaskModel driverTaskModel) {
        if (driverTaskModel != null) {
            driver_name_tv.setText( driverTaskModel.getDriver_first_name() + " " + driverTaskModel.getDriver_last_name() );
            location_tv.setText( driverTaskModel.getAddress() );
        } /*else  {
            main_ll.setVisibility( View.GONE );
        }*/

    }


    private void setThisPage(DriverTaskListListModel driverTaskListListModel) {

        if (driverTaskListListModel != null) {
            setAdsList( driverTaskListListModel );

        }
    }

    private void setAdsList(DriverTaskListListModel driverTaskListListModel) {
        list.clear();

        if (driverTaskListListModel != null && list != null) {
            list.addAll( driverTaskListListModel.getDriverTaskModels() );
            if (adapter != null) {
                synchronized (adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
            if (list.size() <= 0) {
//                showEmptyPage();
            } else {
                initRecyclerView();
            }
        }
    }


    private void setUpList(DriverTaskListListModel driverTaskListListModel) {
        if (driverTaskListListModel != null && list != null) {
            list.clear();
            list.addAll( driverTaskListListModel.getDriverTaskModels() );
            Log.d( "checkList", "" + list );
            if (adapter != null) {
                synchronized (adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
          /*  if (list.size() <= 0) {
                showEmptyPage();
            } else {
                showContent();
            }*/
        }
        initRecyclerView();
    }

    /*  private void showEmptyPage() {
          if (progressActivity != null) {
              progressActivity.showEmpty(getResources().getDrawable(R.drawable.app_icon), getString(R.string.empty_category_title),
                      getString(R.string.""));
          }
      }*/
    public void initRecyclerView() {
        adapter = new HomeListAdapter( activity, list );
        home_list_rv.setLayoutManager( linearLayoutManager );
        home_list_rv.setAdapter( adapter );
        home_list_rv.setHasFixedSize( true );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach( context );
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getFragmentManager().findFragmentByTag( TAG ) != null)
            getFragmentManager().findFragmentByTag( TAG ).setRetainInstance( true );
    }

    @Override
    public void onStart() {

       /* if(hint != null) {
            hint.launchAutomaticHintForCall(activity.findViewById(R.id.action_call));
        }*/
//       globalFunctions.launchAutomaticHintForSearch(mainView, getString(R.string.search_title),  getString(R.string.search_description));
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

}
