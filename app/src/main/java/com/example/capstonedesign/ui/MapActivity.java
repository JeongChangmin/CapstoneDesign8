package com.example.capstonedesign.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstonedesign.R;
import com.example.capstonedesign.SimpleTextAdapter;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.FindAllPOIListenerCallback;
import com.skt.Tmap.TMapData.FindPathDataListenerCallback;
import com.skt.Tmap.TMapData.TMapPathType;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapGpsManager.onLocationChangedCallback;
import com.skt.Tmap.TMapLabelInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapView.TMapLogoPositon;

import java.util.ArrayList;

public class MapActivity extends BaseActivity implements GooeyMenu.GooeyMenuInterface, onLocationChangedCallback {
    PermissionManager permissionManager = null; // 권한요청 관리자

    private GooeyMenu mGooeyMenu;

    @Override
    public void onLocationChange(Location location) {
    }

    private TMapView mMapView = null;

    private Context mContext;

    public static String mApiKey = "005d4dd4-3b86-4b9d-a684-a3ca7ef0800a"; // 발급받은 SKT AppKey

    private static final int[] mArrayMapButton = {
            R.id.btnFindAllPoi,
    };

    /*추가 요소들*/
    TMapMarkerItem tempmarker = new TMapMarkerItem();

    TMapPoint ClickPoint = new TMapPoint(0, 0);
    TMapPoint SearchPoint = new TMapPoint(0, 0);
    TMapPoint mPressedPoint = new TMapPoint(0, 0);

    private int m_CoursePointCounter;
    ArrayList<Double> mCoursePointX;
    ArrayList<Double> mCoursePointY;

    Handler mHandler;

    EditText mSearchRequest;

    FrameLayout mSearchFrame;
    RecyclerView recyclerView;
    ArrayList<String> mSearchResult;

    TMapPOIItem mItem;
    ArrayList<TMapPOIItem> mpoiItem;
    SimpleTextAdapter adapter;
    private int mSearchFlag = 0;

    InputMethodManager imm;
    /*추가 요소 끝*/

    private double m_Latitude = 0;
    private double m_Longitude = 0;

    TMapGpsManager gps = null;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionManager.setResponse(requestCode, grantResults); // 권한요청 관리자에게 결과 전달
    }


    /**
     * onCreate()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        setContentView(R.layout.map_activity);

        mGooeyMenu = (GooeyMenu) findViewById(R.id.gooeyMenu);
        mGooeyMenu.setOnMenuListener(this);

        mContext = this;

        permissionManager = new PermissionManager(this); // 권한요청 관리자

        mMapView = new TMapView(this);
        addView(mMapView);


        /*추가 요소들*/
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);
        tempmarker.setIcon(bitmap);
        tempmarker.setPosition(0.5f, 1.0f);

        mSearchResult = new ArrayList<String>();
        mpoiItem = new ArrayList<TMapPOIItem>();
        adapter = new SimpleTextAdapter(mSearchResult);

        mItem = new TMapPOIItem();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        configureMapView();

        initView();

        mCoursePointX = new ArrayList<Double>();
        mCoursePointY = new ArrayList<Double>();
        m_CoursePointCounter = 0;

        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            @Override
            public void granted() {
                gps = new TMapGpsManager(MapActivity.this);
                gps.setMinTime(1000);
                gps.setMinDistance(5);
                gps.setProvider(gps.GPS_PROVIDER);
                gps.OpenGps();
                gps.setProvider(gps.NETWORK_PROVIDER);
                gps.OpenGps();
            }

            @Override
            public void denied() {
                Log.w("LOG", "위치정보 접근 권한이 필요합니다.");
            }
        });

        mMapView.setTMapLogoPosition(TMapLogoPositon.POSITION_BOTTOMRIGHT);

    }

    /**
     * setSKTMapApiKey()에 ApiKey를 입력 한다.
     */
    private void configureMapView() {
        mMapView.setSKTMapApiKey(mApiKey);
    }

    /**
     * initView - 버튼에 대한 리스너를 등록한다.
     */
    private void initView() {
        for (int btnMapView : mArrayMapButton) {
            ImageButton ViewButton = (ImageButton) findViewById(btnMapView);
            ViewButton.setOnClickListener(this);
        }

        mSearchRequest = (EditText) findViewById(R.id.searchPoi);
        mSearchFrame = findViewById(R.id.searchFrame);

        mSearchFrame.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.searchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));
        recyclerView.setAdapter((adapter));

        adapter.setOnItemClickListener(new SimpleTextAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mItem = mpoiItem.get(pos);

                String tempstr = mItem.getPOIPoint().toString();
                String[] tempstrs = tempstr.split("\\s");
                double Latitude = Double.parseDouble(tempstrs[1]);
                double Longitude = Double.parseDouble(tempstrs[3]);
                mMapView.setCenterPoint(Longitude, Latitude);

                mSearchFrame.setVisibility(View.GONE);

                SearchPoint.setLatitude(Latitude);
                SearchPoint.setLongitude(Longitude);

                if (mMapView.getMarkerItemFromID("SearchPoint") != null) {
                    mMapView.removeMarkerItem("SearchPoint");
                }

                tempmarker.setTMapPoint(SearchPoint);
                tempmarker.setName("검색결과");
                mMapView.addMarkerItem("SearchPoint", tempmarker);

                imm.hideSoftInputFromWindow(mSearchRequest.getWindowToken(), 0);
            }
        });


        mMapView.setOnApiKeyListener(new TMapView.OnApiKeyListenerCallback() {
            @Override
            public void SKTMapApikeySucceed() {
                //LogManager.printLog("MapActivity SKTMapApikeySucceed");
            }

            @Override
            public void SKTMapApikeyFailed(String errorMsg) {
                //LogManager.printLog("MapActivity SKTMapApikeyFailed " + errorMsg);
            }
        });


        mMapView.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                //LogManager.printLog("MapActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());

            }
        });

        mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {

            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                //LogManager.printLog("MapActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                if ((mPressedPoint.getLatitude() == point.getLatitude()) && (mPressedPoint.getLongitude() == point.getLongitude())) {
                    ClickPoint.setLatitude(point.getLatitude());
                    ClickPoint.setLongitude(point.getLongitude());

                    if (mMapView.getMarkerItemFromID("ClickPoint") != null) {
                        mMapView.removeMarkerItem("ClickPoint");
                    }

                    tempmarker.setTMapPoint(ClickPoint);
                    tempmarker.setName("임시점");
                    mMapView.addMarkerItem("ClickPoint", tempmarker);
                }

                //LogManager.printLog("MapActivity onPressUpEvent " + markerlist.size());
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                //LogManager.printLog("MapActivity onPressEvent " + markerlist.size());

                if (mSearchFrame.getVisibility() == View.VISIBLE)
                    mSearchFrame.setVisibility(View.GONE);

                mPressedPoint.setLatitude(point.getLatitude());
                mPressedPoint.setLongitude(point.getLongitude());

                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    //LogManager.printLog("MapActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
                }
                return false;
            }
        });

        mMapView.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point) {
                //LogManager.printLog("MapActivity onLongPressEvent " + markerlist.size());
            }
        });

        mMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                String strMessage = "";
                strMessage = "ID: " + markerItem.getID() + " " + "Title " + markerItem.getCalloutTitle();
                Common.showAlertDialog(MapActivity.this, "Callout Right Button", strMessage);
            }
        });

        mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {
                if (findReverseLabel != null) {
                    //LogManager.printLog("MapActivity setOnClickReverseLabelListener " + findReverseLabel.id + " / " + findReverseLabel.labelLat
                    //+ " / " + findReverseLabel.labelLon + " / " + findReverseLabel.labelName);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gps != null) {
            gps.CloseGps();
        }
    }

    /**
     * onClick Event
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFindAllPoi:
                findAllPoi();
                break;
        }
    }

    /**
     * mapZoomIn
     * 지도를 한단계 확대한다.
     */
    public void mapZoomIn() {
        mMapView.MapZoomIn();
    }

    /**
     * mapZoomOut
     * 지도를 한단계 축소한다.
     */
    public void mapZoomOut() {
        mMapView.MapZoomOut();
    }

    /**
     * 경로 저장
     */
    public void courseSave() {
        MapData temp = new MapData(mCoursePointX, mCoursePointY);

        Intent intent = new Intent();
        intent.putExtra("KEY", temp);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * drawMapPath
     * 지도에 시작-종료 점에 대해서 경로를 표시한다.
     */
    public void drawMapPath() {

        TMapData tmapdata = new TMapData();

        TMapMarkerItem pointmarker = new TMapMarkerItem();

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);

        pointmarker.setIcon(bitmap);
        pointmarker.setPosition(0.5f, 1.0f);

        TMapPoint pastpoint = new TMapPoint(0, 0);


        if (m_CoursePointCounter == 0) {
            mCoursePointX.add(ClickPoint.getLatitude());
            mCoursePointY.add(ClickPoint.getLongitude());

            //마커 설정
            pointmarker.setTMapPoint(ClickPoint);
            pointmarker.setName("시작점");
            mMapView.addMarkerItem("pointmarker" + m_CoursePointCounter, pointmarker);

            m_CoursePointCounter++;
        } else if (m_CoursePointCounter > 0) {
            mCoursePointX.add(ClickPoint.getLatitude());
            mCoursePointY.add(ClickPoint.getLongitude());

            pointmarker.setTMapPoint(ClickPoint);
            pointmarker.setName("경유지" + m_CoursePointCounter);
            mMapView.addMarkerItem("pointmarker" + m_CoursePointCounter, pointmarker);

            pastpoint.setLatitude(mCoursePointX.get(m_CoursePointCounter - 1));
            pastpoint.setLongitude(mCoursePointY.get(m_CoursePointCounter - 1));

            tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, pastpoint, ClickPoint, new FindPathDataListenerCallback() {

                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    mMapView.addTMapPolyLine("Line" + (m_CoursePointCounter - 1), polyLine);

                }
            });

            m_CoursePointCounter++;

        }
    }

    /**
     * removeMapPath
     * 경로 표시를 삭제한다.
     */
    public void removeMapPath() {

        if (m_CoursePointCounter == 0) {
        } else if (m_CoursePointCounter > 0) {
            m_CoursePointCounter--;

            mMapView.removeTMapPolyLine("Line" + m_CoursePointCounter);

            mCoursePointX.remove(m_CoursePointCounter);
            mCoursePointY.remove(m_CoursePointCounter);

            mMapView.removeMarkerItem("pointmarker" + m_CoursePointCounter);
        }
    }

    /**
     * getCenterPoint
     * 지도의 중심점을 가지고 온다.
     */
    public void getCenterPoint() {
        TMapPoint point = mMapView.getCenterPoint();

        Common.showAlertDialog(this, "", "지도의 중심 좌표는 " + point.getLatitude() + " " + point.getLongitude());
    }

    /**
     * findAllPoi
     * 통합검색 POI를 요청한다.
     */
    public void findAllPoi() {
        final String strData = mSearchRequest.getText().toString();
        TMapData tmapdata = new TMapData();

        mSearchFrame.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        if (mpoiItem.isEmpty() == false) {
            mpoiItem.clear();
        }

        if (mSearchResult.isEmpty() == false) {
            mSearchFlag = 1;
            mSearchResult.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        tmapdata.findAllPOI(strData, new FindAllPOIListenerCallback() {
            @Override
            public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                for (int i = 0; i < poiItem.size(); i++) {
                    mItem = poiItem.get(i);
                    mpoiItem.add(mItem);
                    mSearchResult.add(mItem.getPOIName());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {

    }

    @Override
    public void menuItemClicked(int menuNumber) {
        switch (menuNumber) {
            case 1:
                mapZoomIn();
                break;
            case 2:
                mapZoomOut();
                break;
            case 3:
                courseSave();
                break;
            case 4:
                drawMapPath();
                break;
            case 5:
                removeMapPath();
                break;
        }
    }
}

