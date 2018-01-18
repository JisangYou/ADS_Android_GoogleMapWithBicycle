package orgs.androidtown.bicycle.model;

/**
 * Created by Jisang on 2017-10-17.
 */

public class BicycleClass {
    private GeoInfoBikeConvenientFacilitiesWGS GeoInfoBikeConvenientFacilitiesWGS;

    public GeoInfoBikeConvenientFacilitiesWGS getGeoInfoBikeConvenientFacilitiesWGS() {
        return GeoInfoBikeConvenientFacilitiesWGS;
    }

    public void setGeoInfoBikeConvenientFacilitiesWGS(GeoInfoBikeConvenientFacilitiesWGS GeoInfoBikeConvenientFacilitiesWGS) {
        this.GeoInfoBikeConvenientFacilitiesWGS = GeoInfoBikeConvenientFacilitiesWGS;
    }

    @Override
    public String toString() {
        return "ClassPojo [GeoInfoBikeConvenientFacilitiesWGS = " + GeoInfoBikeConvenientFacilitiesWGS + "]";
    }
}
