package com.matteobucci.barzelletteacaso.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.matteobucci.barzelletteacaso.model.Favorite;
import com.matteobucci.barzelletteacaso.R;
import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.model.listener.SwipeDismissListViewTouchListener;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */


public class FavoriteFragment extends Fragment implements AbsListView.OnItemClickListener {

    private Favorite favoriti;
    private Context context;
    private List<Barzelletta> listaBarzellettePreferite;
    private ListView listView;
    private TextView emptyView;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static FavoriteFragment newInstance(Context context) {
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.context = context;
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriti = Favorite.getInstance(context);
        favoriti.loadFavorite();
        listaBarzellettePreferite = favoriti.getFavoriteList();
        mAdapter = new ArrayAdapter<Barzelletta>(getActivity(),
        android.R.layout.simple_list_item_1, android.R.id.text1, listaBarzellettePreferite);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorite_fragment, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, mListView.getItemAtPosition(index).toString());
                startActivity(Intent.createChooser(i, "Condividi con"));
                return true;
            }
        });
        listView = (ListView) mListView;
        emptyView = (TextView) view.findViewById(R.id.emptyListError);
        setDismissListeners();
        checkIfIsVuota();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(favoriti != null){
            favoriti.saveFavorite();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this.context,
                "Clicked " + mAdapter.getItem(position).toString(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {

            emptyView.setText(emptyText);

    }

    private void setDismissListeners(){
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    mAdapter.remove(mAdapter.getItem(position));
                                }
                                   mAdapter.notifyDataSetChanged();
                                checkIfIsVuota();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
    }


    private void checkIfIsVuota(){
        if(mAdapter.isEmpty()){
            setEmptyText("Non sono state ancora aggiunte barzellette preferite. Perchè non ne aggiungi qualcuna?");
        }
        else
            setEmptyText("");

    }





}
