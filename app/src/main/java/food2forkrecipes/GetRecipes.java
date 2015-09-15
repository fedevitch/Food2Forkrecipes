package food2forkrecipes;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyubomyr.food2forkrecipes.R;


import ListDataInterface.ListInterface;
import ListDataContainer.RecipesList;

import RecipeDetailsInterface.DetailsInterface;
import RecipeDataContainer.RecipeDetails;

import SearchInterface.SearchQuery;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetRecipes extends Activity {

    final int List_of_recipes = 1;
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
    EditText SearchText;
    TextView DisplayRecipesCount;
    Button PreviousButton;
    Button NextButton;
    ScrollView ScrollField;
    ListView ListOfItems;
    CellAdapter listAdapter;
    String BaseURL = "http://food2fork.com/api";    //base url
    String key = "31a6f30afb8d54d0e8f54b624e200e47";//key



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //params for displaying components
        setContentView(R.layout.activity_get_recipes);
        ChangeSortTypeButton = (Button) findViewById(R.id.ChangeSort);
        DisplayRecipesCount = (TextView) findViewById(R.id.ResultInfo);
        SearchText = (EditText) findViewById(R.id.searchField);
        SearchText.setOnEditorActionListener(new EditText.OnEditorActionListener()
             {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                    if ((actionId == EditorInfo.IME_ACTION_SEARCH)||!(actionId == EditorInfo.IME_FLAG_NO_ENTER_ACTION)){
                        //implement search
                        pageIndex = 1;
                        DisplayType = 2;
                        NextButton.setText(String.valueOf(pageIndex+1)+">");
                        PreviousButton.setText("<");
                        Search_Query = SearchText.getText().toString();
                        displayRecipes(List_of_recipes, sort, Search, Search_Query);
                        return true;
                    }
                    return false;
                }
             }
        );
        ListOfItems = (ListView) findViewById(R.id.listView);

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

        //retrofit query
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BaseURL).build();

        if (displayType == Top_or_Trending) {                       //display top or trending
            ListInterface Response = restAdapter.create(ListInterface.class);
            Response.getList(key, page, sortType, new retrofit.Callback<ListDataContainer.RecipesList>() {
            @Override
            public void success(RecipesList result, Response response) {
                displayList(result);
                DisplayRecipesCount.setText("Recipes founded: "+result.getCount().toString());
            }

            @Override
            public void failure(RetrofitError error) {
                final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                Toast.makeText(getApplicationContext(), "Query error", Toast.LENGTH_LONG).show();
                RecipeShortInfo.setText("Error: " + error.getMessage());
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
                    displayList(result);
                    DisplayRecipesCount.setText("Recipes founded: "+result.getCount().toString());
                }

                @Override
                public void failure(RetrofitError error) {
                    final TextView RecipeShortInfo = new TextView(getApplicationContext());//short info
                    RecipeShortInfo.setText("Error: " + error.getMessage());
                }
            });
            //NextButton.setEnabled(false);
            //PreviousButton.setEnabled(false);
            ChangeSortTypeButton.setText("view Top/Trending");
            ChangeSortTypeButton.setEnabled(true);
            ChangeSortTypeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayRecipes(pageIndex,sort,Top_or_Trending,SearchQuery);
                }
            });
        }

    }

    public void displayList(final RecipesList result){
        listAdapter = new CellAdapter(this, result);
        ListOfItems.setAdapter(listAdapter);
        ListOfItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Getting recipe...", Toast.LENGTH_LONG).show();
                Intent getDetails = new Intent(GetRecipes.this, GetRecipeDetails.class);
                getDetails.putExtra("recipeId", result.getRecipes().get(position).getRecipeId());
                GetRecipes.this.startActivity(getDetails);
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
