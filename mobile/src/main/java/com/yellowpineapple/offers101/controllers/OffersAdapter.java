package com.yellowpineapple.offers101.controllers;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yellowpineapple.offers101.models.Offer;
import com.yellowpineapple.offers101.views.OfferListView;
import com.yellowpineapple.offers101.views.OfferListView_;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/***
 * ADAPTER
 */

public class OffersAdapter extends BaseAdapter implements View.OnLongClickListener, View.OnClickListener {

    @Getter @Setter List<Offer> offers;
    @Getter @Setter boolean loading;
    @Getter Context context;
    @Setter Location currentLocation;

    public interface Listener {
        void onOfferClick(Offer offer, View view);
        void onOfferLongClick(Offer offer, View view);
    }

    @Getter @Setter Listener listener;

    public OffersAdapter(final Context context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (offers != null) {
            count = offers.size();
        }
        if (loading) {
            count++;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        Offer offer = null;
        if (offers != null && position < offers.size()) {
            offer = offers.get(position);
        }
        return offer;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view;
        if (!isLoadingView(position)) {
            final OfferListView offerView;
            if (convertView == null) {
                offerView = OfferListView_.build(getContext());
                offerView.setClickable(true);
                offerView.setLongClickable(true);
                offerView.setOnClickListener(this);
                offerView.setOnLongClickListener(this);
            } else {
                offerView = (OfferListView) convertView;
            }
            final Offer offer = offers.get(position);
            offerView.setOffer(offer, currentLocation);
            view = offerView;
        } else {
            TextView loadingView = new TextView(getContext());
            loadingView.setText("Loading...");
            view = loadingView;
        }

        return view;
    }

    boolean isLoadingView(int position) {
        int size = offers != null ? offers.size() : 0;
        return position == size;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof OfferListView) {
            OfferListView offerView = (OfferListView) v;
            if (listener != null) listener.onOfferClick(offerView.getOffer(), offerView);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v instanceof OfferListView) {
            OfferListView offerView = (OfferListView) v;
            if (listener != null) listener.onOfferLongClick(offerView.getOffer(), offerView);
            return true;
        }
        return false;
    }
}