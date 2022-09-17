package com.firstapp.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private Context mCtx;
    private List<Forecast> forecastList;

    public ForecastAdapter(Context mCtx, List<Forecast> forecastList) {
        this.mCtx = mCtx;
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.forecast_layout, null);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Forecast forecast = forecastList.get(position);

        holder.tv_forecastTemperature.setText(forecast.getTemperature());
        holder.tv_forecastCondition.setText(forecast.getCondition());
        holder.tv_forecastWeekday.setText(forecast.getWeekday());

    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder{

        TextView tv_forecastTemperature, tv_forecastCondition, tv_forecastWeekday;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_forecastTemperature = itemView.findViewById(R.id.tv_forecastTemperature);
            tv_forecastCondition = itemView.findViewById(R.id.tv_forecastCondition);
            tv_forecastWeekday = itemView.findViewById(R.id.tv_forecastWeekday);
        }
    }
}
