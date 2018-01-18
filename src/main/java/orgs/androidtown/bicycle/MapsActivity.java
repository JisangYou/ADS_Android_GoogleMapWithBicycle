package orgs.androidtown.bicycle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import orgs.androidtown.bicycle.model.BicycleClass;
import orgs.androidtown.bicycle.model.Row;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        // 맵이 사용할 준비가 되었는지를 비동기로 확인하는 작업

        // 맵이 사용할 준비가 되었으면 -> onMapReadyCallback.onMapReady를 호출
        load();
    }

    private void load() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return Remote.getData("http://openapi.seoul.go.kr:8088/47516265416a697337374e7872556a/json/GeoInfoBikeConvenientFacilitiesWGS/1/100");
            }

            @Override
            protected void onPostExecute(String s) {
                Gson gson = new Gson();
                BicycleClass bic = gson.fromJson(s, BicycleClass.class);
                rows = bic.getGeoInfoBikeConvenientFacilitiesWGS().getRow();
                Log.d("MapsActivity", "================" + rows);
                mapFragment.getMapAsync(MapsActivity.this);
                //맵이 사용할 준비가 되었으면 -> onMapReadyCallbac. onMapReady를 호출
            }
        }.execute();
    }

    //좌표 데이터를 저장하기 위한 저장소
    Row[] rows = null;
    //데이터를 사용해서 마크를 각 좌표에 출력

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point = null;
        // Add a marker in Sydney and move the camera
        for (Row row : rows) {
            point = new LatLng(Double.parseDouble(row.getLAT()), Double.parseDouble(row.getLNG()));
            mMap.addMarker(new MarkerOptions().position(point).title(row.getCLASS()));

        }
        point = new LatLng(37.553239, 126.972344);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
    }

}
