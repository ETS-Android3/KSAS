package com.albertocasasortiz.ksas.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albertocasasortiz.ksas.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityReport extends AppCompatActivity {

    private float averageError;
    private int numberOfSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        this.averageError = 0;
        this.numberOfSessions = 0;
        LineChart chart = (LineChart) findViewById(R.id.chart);

        LineData lines = new LineData(getDataFromCsv());
        chart.setData(lines);
        chart.animateXY(2000, 2000);
        chart.invalidate();

        TextView tvAverageError = (TextView) findViewById(R.id.textViewAverageError);
        tvAverageError.setText(String.format(Locale.getDefault(), "%.3f", this.averageError));

        TextView tvNumberExecutions = (TextView) findViewById(R.id.textViewNumberExecutions);
        tvNumberExecutions.setText(String.format(Locale.getDefault(), "%d", this.numberOfSessions));

        Button bShare = (Button) findViewById(R.id.buttonShare);
        bShare.setOnClickListener(view -> {
            View preView;
            preView = getWindow().getDecorView();
            try{
                Bitmap bitmap = Bitmap.createBitmap(preView.getWidth(), preView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                preView.draw(canvas);

                view.setDrawingCacheEnabled(false);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "title");
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);


                OutputStream outstream;
                try {
                    outstream = getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share Image"));
            }catch (Throwable tr){
                Log.d("error", "Couldn't save screenshot", tr);
            }
        });
    }

    private ILineDataSet getDataFromCsv() {
        ILineDataSet dataset;

        FileInputStream fileInputStream;
        String res = "";
        try {
            fileInputStream = new FileInputStream(this.getExternalFilesDir(null) + "/KSAS training sessions/trainingErrors.csv");
            int i;
            StringBuilder buffer = new StringBuilder();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }
            res = buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] splitted = res.split("\n");

        ArrayList<Entry> valueSet = new ArrayList<>();
        int i = 0;
        int error = 0;
        for(String str : splitted) {
            String[] pair = str.split(",");
            error += Integer.parseInt(pair[1].trim());
            valueSet.add(new Entry(i, Integer.parseInt(pair[1].trim())));
            i++;
        }

        this.numberOfSessions = splitted.length;
        this.averageError = error / (float) this.numberOfSessions;

        dataset = new LineDataSet(valueSet, "Errors");

        return dataset;
    }


}