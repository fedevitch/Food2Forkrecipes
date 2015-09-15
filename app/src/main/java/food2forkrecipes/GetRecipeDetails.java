package food2forkrecipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyubomyr.food2forkrecipes.R;
import com.squareup.picasso.Picasso;

import RecipeDataContainer.RecipeDetails;
import RecipeDetailsInterface.DetailsInterface;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lyubomyr on 03.09.2015.
 */
public class GetRecipeDetails extends Activity {

    String BaseURL = "http://food2fork.com/api";    //base url
    String key = "31a6f30afb8d54d0e8f54b624e200e47";//key

    public GetRecipeDetails(){

    }

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Intent intent = getIntent();
        String recipeId = intent.getStringExtra("recipeId");
        //Toast.makeText(getApplicationContext(), "Getting recipe with id: "+recipeId, Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_get_recipe_details);

        //retrofit query
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BaseURL).build();
        DetailsInterface Response = restAdapter.create(DetailsInterface.class);
        Response.getList(key, recipeId, new retrofit.Callback<RecipeDataContainer.RecipeDetails>() {
            @Override
            public void success(RecipeDetails result, retrofit.client.Response response) {
                final TextView title = (TextView) findViewById(R.id.textViewTitle);
                final TextView details = (TextView) findViewById(R.id.textDetails);
                final ImageView image = (ImageView) findViewById(R.id.imageView);
                final RatingBar socialRank = (RatingBar) findViewById(R.id.ratingBarDetailed);
                final TextView imgLink = (TextView) findViewById(R.id.viewImage);
                final TextView f2fLink = (TextView) findViewById(R.id.viewOnf2f);
                final TextView sourceLink = (TextView) findViewById(R.id.viewSource);

                Picasso.with(getApplicationContext())
                        .load(result.getRecipe().getImageUrl())
                        .resize(180, 180)
                        .into(image);
                title.setText(result.getRecipe().getTitle());
                socialRank.setRating(result.getRecipe().getSocialRank().floatValue() / 10);

                int detailsSize = result.getRecipe().getIngredients().size();
                String detailsText = "";
                for (int i = 0; i < detailsSize; i++){
                    detailsText += result.getRecipe().getIngredients().get(i) + "\n";
                }
                details.setText(detailsText);

                String imageText = "<a href=\""+ result.getRecipe().getImageUrl() +"\">"+imgLink.getText()+"</a>";
                imgLink.setText(Html.fromHtml(imageText));
                imgLink.setMovementMethod(LinkMovementMethod.getInstance());
                imgLink.setAutoLinkMask(Linkify.WEB_URLS);
                String f2flinkText = "<a href=\""+ result.getRecipe().getF2fUrl() +"\">"+f2fLink.getText()+"</a>";
                f2fLink.setText(Html.fromHtml(f2flinkText));
                f2fLink.setMovementMethod(LinkMovementMethod.getInstance());
                f2fLink.setAutoLinkMask(Linkify.WEB_URLS);
                String sourceText = "<a href=\""+ result.getRecipe().getSourceUrl() +"\">"+sourceLink.getText()+"</a>";
                sourceLink.setText(Html.fromHtml(sourceText));
                sourceLink.setMovementMethod(LinkMovementMethod.getInstance());
                sourceLink.setAutoLinkMask(Linkify.WEB_URLS);

            }


            @Override
            public void failure(RetrofitError error) {
                //RecipeShortInfo.setText("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Query error", Toast.LENGTH_LONG).show();
            }
        });
    }

}
