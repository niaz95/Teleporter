package edu.monash.fit3027.teleporter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.monash.fit3027.teleporter.models.Job;

/**
 * Created by niaz on 6/6/17.
 */

public class UserJobsList extends ArrayAdapter<Job> {

    private Activity context;
    private List<Job> jobList;

    public UserJobsList(Activity context, List<Job> jobList){
        super(context, R.layout.list_jobs_user, jobList);
        this.context = context;
        this.jobList = jobList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_jobs_user, null, true);
        //Connect UI elements
        TextView itemNameTextView = (TextView) listViewItem.findViewById(R.id.itemNameTextView);
        TextView recipientNameTextView = (TextView) listViewItem.findViewById(R.id.recipientTextView);



        Job job = jobList.get(position);

        //Set colour of job based on delivery status
        String deliveryStatus = job.getM_sDeliveryType();
        if(deliveryStatus.equals("Picked Up")){
            itemNameTextView.setTextColor(Color.YELLOW);
            recipientNameTextView.setTextColor(Color.YELLOW);
        }else if(deliveryStatus.equals("Completed")){
            itemNameTextView.setTextColor(Color.GREEN);
            recipientNameTextView.setTextColor(Color.GREEN);
        }else{
            itemNameTextView.setTextColor(Color.RED);
            recipientNameTextView.setTextColor(Color.RED);
        }
        itemNameTextView.setText(job.getM_sItemName());
        recipientNameTextView.setText(job.getM_sRecipientName());

        return listViewItem;
    }

}
