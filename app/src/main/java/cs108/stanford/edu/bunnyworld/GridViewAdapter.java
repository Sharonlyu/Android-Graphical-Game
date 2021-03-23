package cs108.stanford.edu.bunnyworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter{
    private Context context;
    ArrayList<ShapeRes> shapeResList;

    public GridViewAdapter(Context context, ArrayList<ShapeRes> list) {
        this.context = context;
        shapeResList = list;
    }

    @Override
    public int getCount() {
        return shapeResList.size();
    }

    @Override
    public Object getItem(int i) {
        return shapeResList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.single_grid, null);
        }

        ImageView image = (ImageView) view.findViewById(R.id.grid_image);
        image.setImageResource(shapeResList.get(i).getResId());

        return view;
    }
}
