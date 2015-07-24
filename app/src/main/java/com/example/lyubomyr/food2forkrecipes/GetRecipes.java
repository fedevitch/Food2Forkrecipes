package com.example.lyubomyr.food2forkrecipes;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import retrofit.RestAdapter;
import ListDataInterface.ListInterface;
import RecipeDetailsInterface.DetailsInterface;
import ListDataContainer.RecipesList;
import RecipeDataContainer.RecipeDetails;
import SearchInterface.SearchQuery;


import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetRecipes extends Activity {

    final int List_of_recipes = 1;
    final int Detail_of_recipe = 2;
    final String TopRated = "r";
    final String Trending = "t";
    final int Top_or_Trending = 1;
    final int Search = 2;
    public int DisplayType = 1;
    protected String Search_Query;
    LinearLayout llt;
    protected int pageIndex;
    String sort;
    Button ChangeSortTypeButton;
    Button SearchButton;
    EditText SearchText;
    Button PreviousButton;
    Button NextButton;
    ScrollView ScrollField;

    String BaseURL = "http://food2fork.com/api";    //base url
    String key = "31a6f30afb8d54d0e8f54b624e200e47";//key



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //params for displaying components
        setContentView(R.layout.activity_get_recipes);
        ChangeSortTypeButton = (Button) findViewById(R.id.ChangeSort);


        SearchText = (EditText) findViewById(R.id.searchField);
        SearchButton = (Button) findViewById(R.id.SearchButton);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement search
                pageIndex = 1;
                DisplayType = 2;
                NextButton.setText(String.valueOf(pageIndex+1)+">");
                PreviousButton.setText("<");
                Search_Query = SearchText.getText().toString();
                displayRecipes(List_of_recipes, sort, Search, Search_Query);
            }
        });

        PreviousButton = (Button) findViewById(R.id.prev);
        NextButton = (Button) findViewById(R.id.next);
        pageIndex = 1;
        sort = Trending;
        displayRecipes(pageIndex, sort, Top_or_Trending, "");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displayRecipes(int page, final String sortType, final int displayType, final String SearchQuery){
        Toast.makeText(getApplicationContext(), "Downloading list, please wait...", Toast.LENGTH_LONG).show();
        DisplayType = displayType;
        ScrollField = (ScrollView) findViewById(R.id.scrollView);
        ScrollField.scrollTo(0,0);
        llt = (LinearLayout) findViewById(R.id.container);
        PreviousButton.setEnabled(false);
        NextButton.setEnabled(false);
        try{
            llt.removeAllViewsInLayout();
            llt.clearDisappearingChildren();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        final LinearLayout.LayoutParams lTitleParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lTextParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lImgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        //retrofit query
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BaseURL).build();

        if (displayType == Top_or_Trending) {                       //display top or trending
            ListInterface Response = restAdapter.create(ListInterface.class);
            Response.getList(key, page, sortType, new retrofit.Callback<ListDataContainer.RecipesList>() {
            @Override
            public void success(RecipesList result, Response response) {
                for (int i = 0; i < result.getCount(); i++) {
                    final TextView Title = new TextView(getApplicationContext());//title
                    final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                    final ImageView ItemImage = new ImageView(getApplicationContext());//image
                    final Button viewDetails = new Button(getApplicationContext());//button for view details

                    //init objects for display top recipes
                    Title.setLayoutParams(lTitleParams);        //title init
                    Title.setTextSize(20);
                    Title.setTextColor(Color.BLACK);
                    Title.setText(Html.fromHtml(result.getRecipes().get(i).getTitle()));

                    RecipeShortInfo.setLayoutParams(lTextParams);//short info init
                    RecipeShortInfo.setTextColor(Color.BLACK);
                    RecipeShortInfo.setText("\n Social rank: " + result.getRecipes().get(i).getSocialRank()
                                    + "\n Publisher: " + result.getRecipes().get(i).getPublisher()
                    );

                    ItemImage.setLayoutParams(lImgParams);//image init
                    String imageURL = result.getRecipes().get(i).getImageUrl();
                    Picasso.with(getApplicationContext()).load(imageURL).into(ItemImage);

                    final String tempID = result.getRecipes().get(i).getRecipeId();
                    viewDetails.setLayoutParams(lButtonParams);//button init
                    viewDetails.setText("Details...");
                    viewDetails.setTextColor(Color.BLACK);
                    viewDetails.setId(i);
                    viewDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DisplayRecipe(tempID);
                        }
                    });

                    llt.addView(Title);
                    llt.addView(RecipeShortInfo);
                    llt.addView(ItemImage);
                    llt.addView(viewDetails);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                RecipeShortInfo.setText("Error" + error.getMessage());
            }
        });
            //set listeners for navigation buttons
            NextButton.setEnabled(true);
            NextButton.setText(String.valueOf(pageIndex + 1) + " >");
            if (pageIndex > 1){
                PreviousButton.setEnabled(true);
                PreviousButton.setText("< " + String.valueOf(pageIndex - 1));
            }
            else{
                PreviousButton.setEnabled(false);
            }
            PreviousButton = (Button) findViewById(R.id.prev);
            PreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRecipes(--pageIndex, sort, DisplayType, TopRated);
                }
            });
            NextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRecipes(++pageIndex, sort, DisplayType, TopRated);
                }
            });
            ChangeSortTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageIndex = 1;
                    PreviousButton.setText("<");
                    if (TopRated.contentEquals(sort)) {
                        sort = Trending;
                        ChangeSortTypeButton.setText("sorted by Trending");
                        displayRecipes(pageIndex, Trending, DisplayType, Search_Query);
                        return;
                    }
                    if (Trending.contentEquals(sort)) {
                        sort = TopRated;
                        ChangeSortTypeButton.setText("sorted by Top Rated");
                        displayRecipes(pageIndex, TopRated, DisplayType, Search_Query);
                        return;
                    }
                    else {
                        sort = Trending;
                        ChangeSortTypeButton.setText("sorted by Trending");
                        displayRecipes(pageIndex, sort, DisplayType, Search_Query);
                        return;
                    }
                }
            });
            ChangeSortTypeButton.setEnabled(true);
            if (sort.equals(TopRated)){
                ChangeSortTypeButton.setText("sorted by Top Rated");
            }
            if (sort.equals(Trending)){
                ChangeSortTypeButton.setText("sorted by Trending");
            }
        }
        if (displayType == Search){                             //display search results
            SearchQuery Response = restAdapter.create(SearchQuery.class);
            Response.getList(key, SearchQuery, new retrofit.Callback<ListDataContainer.RecipesList>() {
                @Override
                public void success(RecipesList result, Response response) {
                    for (int i = 0; i < result.getCount(); i++) {
                        final TextView Title = new TextView(getApplicationContext());//title
                        final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                        final ImageView ItemImage = new ImageView(getApplicationContext());//image
                        final Button viewDetails = new Button(getApplicationContext());//button for view details

                        //init objects for display top recipes
                        Title.setLayoutParams(lTitleParams);        //title init
                        Title.setTextSize(20);
                        Title.setTextColor(Color.BLACK);
                        Title.setText(Html.fromHtml(result.getRecipes().get(i).getTitle()));

                        RecipeShortInfo.setLayoutParams(lTextParams);//short info init
                        RecipeShortInfo.setTextColor(Color.BLACK);
                        RecipeShortInfo.setText("\n Social rank: " + result.getRecipes().get(i).getSocialRank()
                                        + "\n Publisher: " + result.getRecipes().get(i).getPublisher()
                        );

                        ItemImage.setLayoutParams(lImgParams);//image init
                        String imageURL = result.getRecipes().get(i).getImageUrl();
                        Picasso.with(getApplicationContext()).load(imageURL).into(ItemImage);

                        final String tempID = result.getRecipes().get(i).getRecipeId();
                        viewDetails.setLayoutParams(lButtonParams);//button init
                        viewDetails.setText("Details...");
                        viewDetails.setTextColor(Color.BLACK);
                        viewDetails.setId(i);
                        viewDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DisplayRecipe(tempID);
                            }
                        });

                        llt.addView(Title);
                        llt.addView(RecipeShortInfo);
                        llt.addView(ItemImage);
                        llt.addView(viewDetails);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                    RecipeShortInfo.setText("Error" + error.getMessage());
                }
            });
            NextButton.setEnabled(false);
            PreviousButton.setEnabled(false);
            ChangeSortTypeButton.setText("view Top Rated");
            ChangeSortTypeButton.setEnabled(true);
            ChangeSortTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRecipes(pageIndex,sort,Top_or_Trending,SearchQuery);
                }
            });
        }

    }


    public void DisplayRecipe(String id){
        Toast.makeText(getApplicationContext(), "Getting recipe...", Toast.LENGTH_LONG).show();
        ScrollField = (ScrollView) findViewById(R.id.scrollView);
        ScrollField.scrollTo(0,0);
        llt = (LinearLayout) findViewById(R.id.container);
        PreviousButton.setEnabled(false);
        NextButton.setEnabled(false);
        try{
            llt.removeAllViewsInLayout();
            llt.clearDisappearingChildren();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        llt = (LinearLayout) findViewById(R.id.container);
        llt.removeAllViewsInLayout();
        final LinearLayout.LayoutParams lTitleParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lTextParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams lImgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        final ImageView ItemImage = new ImageView(this);                                    //image
        final TextView ExternalLinkImage = new TextView(this);                          //Image link
        final TextView Title = new TextView(this);                                          //title
        final TextView RecipeShortInfo = new TextView(this);                            //short info
        final TextView IngredientsList = new TextView(this);                   //display ingredients
        final TextView ExternalLinkF2F = new TextView(this);                              //F2F link
        final TextView ExternalLinkSource = new TextView(this);                        //Source link

        //retrofit query
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BaseURL).build();
        DetailsInterface Response = restAdapter.create(DetailsInterface.class);
        Response.getList(key, id, new retrofit.Callback<RecipeDataContainer.RecipeDetails>() {
            @Override
            public void success(RecipeDetails result, Response response) {
                Title.setLayoutParams(lTitleParams);                                //title init
                Title.setTextSize(20);
                Title.setText(Html.fromHtml(result.getRecipe().getTitle()));


                RecipeShortInfo.setLayoutParams(lTextParams);                       //shortinfo init
                RecipeShortInfo.setText("\n Social rank: " + result.getRecipe().getSocialRank()
                                + "\n Publisher: " + result.getRecipe().getPublisher()
                );


                IngredientsList.setLayoutParams(lTextParams);    //display ingredients init
                IngredientsList.setTextSize(18);
                String ingredients = "";
                for(int i = 0; i < result.getRecipe().getIngredients().size(); i++){
                    ingredients += "\n"+Html.fromHtml(result.getRecipe().getIngredients().get(i));
                }
                IngredientsList.setText(ingredients);

                ExternalLinkF2F.setLayoutParams(lTextParams);                 //f2flink init
                ExternalLinkF2F.setTextSize(16);
                ExternalLinkF2F.setClickable(true);
                String F2Flink = "<a href=\""+result.getRecipe().getF2fUrl() +"\">View on Food2Fork</a>";
                ExternalLinkF2F.setText(Html.fromHtml(F2Flink));
                ExternalLinkF2F.setMovementMethod(LinkMovementMethod.getInstance());
                ExternalLinkF2F.setAutoLinkMask(Linkify.WEB_URLS);

                ExternalLinkSource.setLayoutParams(lTextParams);          //source link init
                ExternalLinkSource.setClickable(true);
                String Source = "<a href=\""+result.getRecipe().getSourceUrl()+"\">Source</a>";
                ExternalLinkSource.setText(Html.fromHtml(Source));
                ExternalLinkSource.setMovementMethod(LinkMovementMethod.getInstance());
                ExternalLinkSource.setAutoLinkMask(Linkify.WEB_URLS);
                ExternalLinkSource.setTextSize(16);

                ExternalLinkImage.setLayoutParams(lTextParams);              // imglink init
                ExternalLinkImage.setClickable(true);
                String Image = "<a href=\""+result.getRecipe().getImageUrl()+"\">Image</a>";
                ExternalLinkImage.setText(Html.fromHtml(Image));
                ExternalLinkImage.setMovementMethod(LinkMovementMethod.getInstance());
                ExternalLinkImage.setAutoLinkMask(Linkify.WEB_URLS);
                ExternalLinkImage.setTextSize(16);

                ItemImage.setLayoutParams(lImgParams);                          //image init
                String imageURL = result.getRecipe().getImageUrl();
                Picasso.with(getApplicationContext()).load(imageURL).into(ItemImage);
            }


            @Override
            public void failure(RetrofitError error) {
                RecipeShortInfo.setText("Error" + error.getMessage());
            }
        });

        //add elements and display info on view
        llt.addView(Title);
        llt.addView(ItemImage);
        llt.addView(RecipeShortInfo);
        llt.addView(IngredientsList);
        llt.addView(ExternalLinkF2F);
        llt.addView(ExternalLinkSource);
        llt.addView(ExternalLinkImage);

        ChangeSortTypeButton.setText("Recipe");
        ChangeSortTypeButton.setEnabled(false);
        NextButton.setEnabled(false);
        NextButton.setText(">");
        PreviousButton.setText("<");
        PreviousButton.setEnabled(true);
        PreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRecipes(pageIndex, sort, DisplayType, Search_Query);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
