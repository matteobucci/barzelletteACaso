package com.matteobucci.barzelletteacaso;

import com.matteobucci.barzelletteacaso.model.Barzelletta;
import com.matteobucci.barzelletteacaso.view.DettagliAutore;

import java.util.List;

/**
 * Created by Matti on 04/10/2015.
 */
public interface GestoreBarzelletteOnline {

    public Barzelletta getBarzelletta();
    public Barzelletta getBarzellettaPrecedente();
    public DettagliAutore getDettagliAutore();
    public List<Barzelletta> getListaBarzellette();
    public void addMiPiace();
    public void removeMiPiace();
    public void addNonMiPiace();
    public void removeNonMiPiace();


}
