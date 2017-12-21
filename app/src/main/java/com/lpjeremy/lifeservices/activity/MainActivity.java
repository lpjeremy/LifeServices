package com.lpjeremy.lifeservices.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lpjeremy.lifeservices.R;
import com.lpjeremy.lifeservices.activity.base.BaseActivity;
import com.lpjeremy.lifeservices.utils.http.HttpRequestApi;
import com.lpjeremy.lifeservices.utils.http.base.BaseResult;
import com.lpjeremy.lifeservices.utils.http.base.HttpRequestCallBack;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private MapView mapView;
    private AMap aMap;
    private Location curLocation;//定位点
    private String cityCode;
    /**
     * 查询数量每页
     */
    private int pageSize = 50;
    /**
     * 查询范围 XX米
     */
    private int range = 1000;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mapView = getViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        aMap = mapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        //是否显示定位蓝点，true显示 false隐藏
        aMap.setMyLocationEnabled(true);
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.interval(5000);

        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (location != null) {
                    Log.i(TAG, "定位成功 lng = " + location.getLongitude() + "  lat = " + location.getLatitude());
                    curLocation = location;
                    if (TextUtils.isEmpty(cityCode)) {
                        searchWCData("厕所");
                    }
                } else {
                    Toast.makeText(mContext, "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void loadData() {
        HttpRequestApi.getInstance().login("18628047079", "666666", new HttpRequestCallBack<BaseResult>() {
            @Override
            public void onComplete(BaseResult result) {
                if (result.getCode() == 1) {
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "登录失败 err = " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(Throwable throwable) {
                Toast.makeText(mContext, "失败 err = " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchWCData(final String keyWord) {

        locationQuery(curLocation).doOnNext(new Consumer<RegeocodeAddress>() {
            @Override
            public void accept(RegeocodeAddress address) throws Exception {
                Log.i(TAG, "cityCode = " + address.getCityCode());
                cityCode = address.getAdCode();
            }
        }).flatMap(new Function<RegeocodeAddress, ObservableSource<ArrayList<PoiItem>>>() {
            @Override
            public ObservableSource<ArrayList<PoiItem>> apply(RegeocodeAddress regeocodeAddress) throws Exception {
                return searchLocation(keyWord, regeocodeAddress.getCityCode());
            }
        }).subscribe(new Consumer<ArrayList<PoiItem>>() {
            @Override
            public void accept(ArrayList<PoiItem> poiItems) throws Exception {
                //画在地图上
                for (int i = 0; i < poiItems.size(); i++) {
                    Log.i(TAG, poiItems.get(i).getLatLonPoint().getLongitude() + "  " + poiItems.get(i).getLatLonPoint().getLatitude());
                }
                drawPointsToMap(poiItems);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * POI检索关键字
     *
     * @param keyWord
     * @param cityCode
     * @return
     */
    private Observable<ArrayList<PoiItem>> searchLocation(final String keyWord, final String cityCode) {

        return Observable.create(new ObservableOnSubscribe<ArrayList<PoiItem>>() {
            @Override
            public void subscribe(final ObservableEmitter<ArrayList<PoiItem>> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                PoiSearch.Query query = new PoiSearch.Query(keyWord, "", cityCode);
                query.setPageSize(pageSize);//每页显示多少条
                query.setPageNum(0);//查询页码
                final PoiSearch poiSearch = new PoiSearch(mContext, query);
                //设置检索范围
                poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(curLocation.getLatitude(), curLocation.getLongitude()), range));
                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                    @Override
                    public void onPoiSearched(PoiResult poiResult, int i) {
                        if (poiResult != null && poiResult.getPois() != null && !poiResult.getPois().isEmpty()) {
                            emitter.onNext(poiResult.getPois());
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("检索失败"));
                        }
                    }

                    @Override
                    public void onPoiItemSearched(PoiItem poiItem, int i) {

                    }
                });
                poiSearch.searchPOIAsyn();
            }
        }).subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 根据定位点逆地址解析出city信息
     *
     * @param location
     * @return
     */
    private Observable<RegeocodeAddress> locationQuery(Location location) {
        final GeocodeSearch geocodeSearch = new GeocodeSearch(mContext);
        LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        final RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLonPoint, range, GeocodeSearch.AMAP);
        return Observable.create(new ObservableOnSubscribe<RegeocodeAddress>() {
            @Override
            public void subscribe(final ObservableEmitter<RegeocodeAddress> emitter) throws Exception {
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult result, int code) {
                        if (emitter.isDisposed()) return;
                        if (result != null && result.getRegeocodeAddress() != null) {
                            emitter.onNext(result.getRegeocodeAddress());
                            emitter.onComplete();
                        } else {
                            emitter.onError(new Throwable("逆地址解析错误"));
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
                geocodeSearch.getFromLocationAsyn(regeocodeQuery);
            }
        });

    }

    /**
     * 绘制点
     *
     * @param poiItems
     */
    private void drawPointsToMap(ArrayList<PoiItem> poiItems) {
        if (poiItems == null) return;
        //画一个以定位点为中心的圆圈
        LatLng locationLatlng = new LatLng(curLocation.getLatitude(), curLocation.getLongitude());
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(locationLatlng);
        circleOptions.radius(range);
        circleOptions.fillColor(Color.argb(50, 1, 1, 1));
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(5);
        aMap.addCircle(circleOptions);
        //绘制POI点
        ArrayList<MarkerOptions> arrayList = new ArrayList<>();
        for (int i = 0; i < poiItems.size(); i++) {
            PoiItem poiItem = poiItems.get(i);
            LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_orange)));
            markerOptions.setFlat(true);
            arrayList.add(markerOptions);
        }
        aMap.addMarkers(arrayList, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null)
            mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (mapView != null)
            mapView.onSaveInstanceState(outState);
    }


}

