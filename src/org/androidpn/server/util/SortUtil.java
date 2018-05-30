package org.androidpn.server.util;

import org.androidpn.server.model.Bussiness;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

    public static List<Bussiness> sortBussinessesByDistance(List<Bussiness> bussinessList, Location location) {
        if (location == null) {
            return bussinessList;
        }

        BussinessComparatorByDistance bussinessComparatorByDistance = new BussinessComparatorByDistance(location);

        Collections.sort(bussinessList, bussinessComparatorByDistance);

        return bussinessList;
    }

}

class BussinessComparatorByDistance implements Comparator<Bussiness> {

    private Location location;

    public BussinessComparatorByDistance(Location location) {
        this.location = location;
    }


    @Override
    public int compare(Bussiness b1, Bussiness b2) {
        if (location.getDistance(b1) >= location.getDistance(b2)) {
            return 1;
        }
        return -1;
    }
}
