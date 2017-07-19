package info.android.rowing.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import info.android.rowing.R;

/**
 * Created by mahmoud_mashal on 7/13/2017.
 */

public class RowerDataAdapter extends RecyclerView.Adapter<RowerDataAdapter.RowerDataHolder> {

    private Context context;
    private List<RowerData> rowerDataList;

    public RowerDataAdapter(Context context, List<RowerData> rowerDataList) {
        this.context = context;
        this.rowerDataList = rowerDataList;
    }

    @Override
    public RowerDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View column = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_column, parent, false);
        return new RowerDataHolder(column);
    }

    @Override
    public void onBindViewHolder(RowerDataHolder holder, int position) {

        RowerData data = rowerDataList.get(position);
        holder.rowerName.setText(data.getname());
        holder.pace.setText(String.valueOf(data.getSpeed()));
        holder.distance.setText(String.valueOf(data.getDistance()));
        holder.strokeRate.setText(String.valueOf(data.getStrokeRate()));
        holder.angle.setText(String.valueOf(data.getAngle()));
        holder.time.setText(data.gettime());
    }

    @Override
    public int getItemCount() {
        return rowerDataList.size();
    }

    class RowerDataHolder extends RecyclerView.ViewHolder{
        TextView rowerName,strokeRate,pace,distance,angle,time;

        public  RowerDataHolder(View itemView){
            super(itemView);
            rowerName = (TextView) itemView.findViewById(R.id.player_Name);
            pace = (TextView) itemView.findViewById(R.id.speed);
            distance = (TextView) itemView.findViewById(R.id.distance);
            strokeRate = (TextView) itemView.findViewById(R.id.stroke_Rate);
            angle = (TextView) itemView.findViewById(R.id.angle);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }

}
