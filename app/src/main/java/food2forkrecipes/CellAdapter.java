package food2forkrecipes;

/**
 * Created by Lyubomyr on 02.09.2015.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import ListDataContainer.RecipesList;

import com.example.lyubomyr.food2forkrecipes.R;
import com.squareup.picasso.Picasso;

public class CellAdapter extends ArrayAdapter<String> {
    private RecipesList data;
    private Context context;

    public CellAdapter(Context context, RecipesList data){
        super(context, R.layout.listview_cell);
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount(){
        return data.getCount();
    }

    @Override
    public String getItem(int position){
        return ((String) data.getRecipes().get(position).getTitle());
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_cell, parent, false);

        TextView title = (TextView) view.findViewById(R.id.titleItem);
        TextView subtitle = (TextView) view.findViewById(R.id.subtitleItem);
        ImageView image = (ImageView) view.findViewById(R.id.imageItem);
        RatingBar socialRank = (RatingBar) view.findViewById(R.id.ratingBarDetailed);
        RecipesList object = data;

        title.setText(object.getRecipes().get(position).getTitle());
        subtitle.setText(object.getRecipes().get(position).getPublisher());
        socialRank.setRating(object.getRecipes().get(position).getSocialRank().floatValue()/10);
        Picasso.with(getContext())
                .load(object.getRecipes().get(position).getImageUrl())
                .resize(80,80)
                .into(image);

        return view;
    }
}
