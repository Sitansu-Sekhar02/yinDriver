package com.yin.driver.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yin.driver.AppController;
import com.yin.driver.R;
import com.yin.driver.global.GlobalFunctions;
import com.yin.driver.global.GlobalVariables;
import com.yin.driver.map.SearchPlaceOnMapActivity;
import com.yin.driver.services.model.DriverTaskModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeListAdapter extends RecyclerView.Adapter <HomeListAdapter.HomeListViewHolder> {
    public static final String TAG = "HomeListAdapter";
    private final Activity activity;
    private List <DriverTaskModel> list;

    public HomeListAdapter(Activity activity, List <DriverTaskModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeListAdapter.HomeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.row_driver_task_list, parent, false );
        return new HomeListViewHolder( v );
    }

    @Override
    public void onBindViewHolder(HomeListAdapter.HomeListViewHolder holder, int position) {
        final DriverTaskModel model = list.get( position );

        if (model.getT_therapists_name() != null) {
            holder.therapists_name_tv.setText( model.getT_therapists_name() );
        }
        if (model.getTherapist_pickup_time() != null) {
            holder.time_tv.setText( GlobalFunctions.getTimeFromDate( model.getTherapist_pickup_time() ) );
//            holder.time_tv.setText( model.getTherapist_pickup_time() );
        }
        if (model.getTherapist_id() != null) {
            holder.therapists_dno_tv.setText( model.getTherapist_id() );
        }
  /*      if (model.getT_image() != null) {
            Picasso.with( activity ).load( model.getT_image() ).placeholder( R.drawable.ic_default_img ).into( holder.list_iv );
        }*/
        try {
            if (model.getT_image() != null || !model.getT_image().equals( "null" ) || !model.getT_image().equalsIgnoreCase( "" )) {
                Picasso.with( activity ).load( model.getT_image() ).placeholder( R.drawable.ic_girl_icon ).into( holder.therapists_iv );
            }
        } catch (Exception e) {
        }

        if (model.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_WAITING ) || model.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_WAY_To_COMPLETE ) || model.getStatus().equalsIgnoreCase( "27" )) {
            holder.view_details_tv.setVisibility( View.GONE );
        } else {
            holder.view_details_tv.setVisibility( View.VISIBLE );
        }

        if (model.getStatus().equalsIgnoreCase( GlobalVariables.DRIVER_TASK_UPDATE_WAITING )) {
            holder.main_ll.setVisibility( View.GONE );
        } else {
            holder.main_ll.setVisibility( View.VISIBLE );
        }

        holder.view_details_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SearchPlaceOnMapActivity.newInstance( activity, model );
                activity.startActivity( intent );
            }
        } );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeListViewHolder extends RecyclerView.ViewHolder {
        CircleImageView driver_iv, therapists_iv;
        TextView driver_name_tv, driver_location_tv, customer_request_tv,
                therapists_name_tv, therapists_dno_tv, view_details_tv,
                time_tv;
        LinearLayout main_ll;

        public HomeListViewHolder(@NonNull View itemView) {
            super( itemView );
//            driver_iv = itemView.findViewById( R.id.row_list_news_list_driver_iv );
            therapists_iv = itemView.findViewById( R.id.row_list_therapists_iv );
//            driver_name_tv = itemView.findViewById( R.id.row_list_news_list_driver_name_tv );
//            driver_location_tv = itemView.findViewById( R.id.row_list_location_tv );
//            customer_request_tv = itemView.findViewById( R.id.row_list_customer_request_tv );
            therapists_name_tv = itemView.findViewById( R.id.row_list_news_list_therapists_name_tv );
            therapists_dno_tv = itemView.findViewById( R.id.row_list_therapists_dno_tv );
            view_details_tv = itemView.findViewById( R.id.row_list_view_details_tv );
            time_tv = itemView.findViewById( R.id.row_list_time_tv );
            main_ll = itemView.findViewById( R.id.main_ll );
        }
    }
}
