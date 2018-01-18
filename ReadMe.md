# ADS04 Android

## 수업 내용

- 구글맵 세팅 및 간단한 예제 학습
- 데이터 API 사용방법 학습

## Code Review

### MapActivity

```Java
ublic class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
```

### Remote

```Java
public class Remote {

    public static String getData(String string){
        final StringBuilder result = new StringBuilder();
        boolean runflag = true;
        while (runflag) {
            try {
                URL url = new URL(string);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                // 통신이 성공인지 체크
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 여기서 부터는 파일에서 데이터를 가져오는 것과 동일
                    InputStreamReader isr = new InputStreamReader(con.getInputStream());
                    BufferedReader br = new BufferedReader(isr);
                    String temp = "";
                    while ((temp = br.readLine()) != null) {
                        result.append(temp).append("\n");
                    }
                    br.close();
                    isr.close();
                } else {
                    Log.e("ServerError", con.getResponseCode() + "");
                }
                con.disconnect();
                runflag= false;
            } catch (Exception e) {
               runflag = true;
                Log.e("Error", e.toString());
            }
        }
        return result.toString();
    }

}

```

### Model Package

※ Json 형식을 POJO Class로 변환해주는 site에서 공공 API데이터 JSON 데이터를 넣으면 모델 클래스 형태로 변환해줌.
※ 아래의 클래스들은 변환해준 모델 클래스들의 형태를 복사+붙여넣기 한것임
※ 모델클래스들의 변수명들은 json데이터와 동일하게 한다. 

- BicycleClass
- GeoInfoBikeConvenientFacilitiesWGS
- RESULT
- Row


## 보충설명

### 설치과정

- 참고사이트로 대체

[구글맵설치과정](http://webnautes.tistory.com/647)

### Google Maps 기본세팅

```Java
public class MapPane extends Activity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng sydney = new LatLng(-33.867, 151.206);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }
}
```

[GoogleMapsApi참고](https://developers.google.com/maps/?hl=ko)

### +@ LocationManager

- LocationManger은 현재폰에 제공되는 위치제공자 확인을 할 수 있는 기능이 있다. 
- 일반적으로 안드로이드 폰에서 위치정보를 얻는 방법은 크게 3가지가 있다.

```Java
1. GPS - 위성에서 정보를 받아 삼각측량으로 위치를 계산, 정확하다, 건물 안에서는 안된다
2. 3G망 - 인접된 전화기지국에서오는 전파의 시간 차이로 위치를 계산, 실내에서도 가능
3. WiFi 의 AP
```

- 안드로이드 앱에서 위치제공자 정보를 알아내려면 다음과 같은 절차가 있다.

``` Java
Step1 : 메니페스트 파일에 권한 획득 
Step2 : 시스템으로부터 LocationManager 객체 얻어오기
Step3 : getAllProviders() 메소드로 위치제공자 리스트 가져오기.
```

- 위치제공자 (LocationManager)를 통해 위치정보 (위도, 경도, 고도 ..  )등을 받아오는 예제

```Java
1. requestLocationUpdates() 메소드를 통해 GPS 나 NETWORK로부터 위치정보 업데이트를 받아오는 것이고
2. 이때 LocationListener 리스너를 등록하여 줍니다
3. LocationListener 에는 몇가지 메소드가 있는데,  그중에서 onLocationChanged() 에서 위치 업데이트 값을 받아오면 됩니다.
```

- 위치정보 예제 다룰때 주의할 점

```Java
1. GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다

2. 실내에서는 GPS_PROVIDER 를 요청해도 응답이 없다.  특별한 처리를 안하면 아무리 시간이 지나도 응답이 없습니다

   해결방법은

    ① 타이머를 설정하여 GPS_PROVIDER 에서 일정시간 응답이 없는 경우 NETWORK_PROVIDER로 전환

    ② 혹은, 둘다 한꺼번헤 호출하여 들어오는 값을 사용하는 방식. (일반적)
```


### 출처

- 출처 : http://webnautes.tistory.com/647
- 출처: http://bitsoul.tistory.com/130 [Happy Programmer~]
- 출처: http://bitsoul.tistory.com/131 [Happy Programmer~]

## TODO

- 추후에 googleMap 관련 다양한 기능들을 사용해 __업데이트 할 예정__ 이다.
- 직접 데이터를 만들어 보기.(크롤링관련해서도 정보 찾아보기)

## Retrospect

- 이번에 학습한 내용은 구글맵을 사용하기 위한 세팅정도 였기때문에, 구글맵에 관한 다양한 기능들을 사용해보지 못했다. 
- 개인적으로 사용해보고 싶은 기능들이 있으므로, 추후에 개인 프로젝트를 하면서 사용해볼 예정이다.

## Output

![bicyclemaplately](https://user-images.githubusercontent.com/31605792/35096925-531d324a-fc91-11e7-8d80-8bc9b82c3472.gif)
