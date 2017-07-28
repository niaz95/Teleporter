package edu.monash.fit3027.teleporter;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



import java.util.List;
import java.util.Locale;

import edu.monash.fit3027.teleporter.models.Job;

/**
 * Created by niaz on 8/6/17.
 */

public class TeleporterJobsList extends ArrayAdapter<Job> {

    private Activity context;
    private List<Job> jobList;
    private Geocoder geocoder;
    private List<Address> addresses;


    public TeleporterJobsList(Activity context, List<Job> jobList) {
        super(context, R.layout.list_jobs_teleporter, jobList);
        //Get context and list where the items will be implemented
        this.context = context;
        this.jobList = jobList;
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_jobs_teleporter, null, true);


        //Connect UI elements
        TextView startAddTextView = (TextView) listViewItem.findViewById(R.id.startAddTextView);
        TextView endAddTextView = (TextView) listViewItem.findViewById(R.id.endAddTextView);
        TextView itemTypeTextView = (TextView) listViewItem.findViewById(R.id.itemType1TextView);

        Job job = jobList.get(position);

        try {
            //Get addresses from Geocoder using the provided Lat andLng values
            addresses = geocoder.getFromLocation(job.getM_sStartLat(), job.getM_sStartLng(), 1);
            String startaddress = addresses.get(0).getLocality();
            startAddTextView.setText(startaddress);

            addresses = geocoder.getFromLocation(job.getM_sEndLat(), job.getM_sEndLng(), 1);
            String endaddress = addresses.get(0).getLocality();
            endAddTextView.setText(endaddress);

        }catch(Exception e){
            System.out.print("Error: "+e.getMessage());
        }

        itemTypeTextView.setText(job.getM_sItemType());

        return listViewItem;

    }
}
