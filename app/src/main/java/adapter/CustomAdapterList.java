package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trivedi.birthdayreminder.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import dataclass.DataList;

/**
 * Created by TI A1 on 29-05-2017.
 */
public class CustomAdapterList extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataList> movieItems;
    List<DataList> arrDataFilter;
    static boolean flag=false;

    public CustomAdapterList(Activity activity, List<DataList> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
        arrDataFilter = movieItems;
        arrDataFilter = new ArrayList<DataList>();
        arrDataFilter.addAll(movieItems);
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_list, null);

        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth(); // ((display.getWidth()*20)/100)
        int height = display.getHeight();// ((display.getHeight()*30)/100)

        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        TextView tv_bdate = (TextView)convertView.findViewById(R.id.tv_bdate);
        ImageView img_profile = (ImageView)convertView.findViewById(R.id.img_profile);
       /* TextView tvreservation_date = (TextView) convertView.findViewById(R.id.tvreservation_date);
        TextView tvgross_total = (TextView) convertView.findViewById(R.id.tvgross_total);
        TextView tvemailid = (TextView) convertView.findViewById(R.id.tvemailid);
        */
        final DataList m = movieItems.get(position);
        tv_bdate.setText(m.getBdate()+"");
        tv_name.setText(m.getName()+"");
        File imgFile = new File(m.getImage()); // path of your file

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imgFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inPurgeable = true;
        options.inScaled = true;
        Bitmap bm = BitmapFactory.decodeStream(fis, null,options);
        img_profile.setImageBitmap(bm);
        return convertView;
    }
}
