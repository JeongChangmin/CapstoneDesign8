package com.example.capstonedesign.ui;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.capstonedesign.LogManager;
import com.example.capstonedesign.R;
import com.skt.Tmap.TMapData;
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

public class MapActivity2 extends BaseActivity2 implements onLocationChangedCallback {
    PermissionManager permissionManager = null; // 권한요청 관리자

    @Override
    public void onLocationChange(Location location) {   //위치 변화가 감지되면 실행
        if (!m_PathDraw) {
            drawMapPath();
            m_PathDraw = !m_PathDraw;
        }
        if (m_bTrackingMode) {
            mMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    private TMapView mMapView = null;

    private Context mContext;

    public static String mApiKey = "005d4dd4-3b86-4b9d-a684-a3ca7ef0800a"; // 발급받은 SKT AppKey

    private static final int[] mArrayMapButton = {
            R.id.btnGPS
    };

    /*추가 요소들*/
    TMapMarkerItem tempmarker = new TMapMarkerItem();

    TMapPoint ClickPoint = new TMapPoint(0, 0);

    MapData mCourse;

    private int m_CoursePointCounter;
    ArrayList<Double> mCoursePointX;
    ArrayList<Double> mCoursePointY;

    private boolean m_bShowMapIcon = true;

    private boolean m_bSightVisible = true;
    private boolean m_bTrackingMode = true;
    private boolean m_PathDraw = false;

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

        setContentView(R.layout.map_activity2);

        mContext = this;

        permissionManager = new PermissionManager(this); // 권한요청 관리자

        mMapView = new TMapView(this);

        mCourse = new MapData(mCoursePointX, mCoursePointY);

        mCourse = getIntent().getParcelableExtra("course");

        mMapView.setZoomLevel(14);
        setMapIcon();
        setCompassMode();
        setSightVisible();
        setTrackingMode();

        addView(mMapView);


        /*추가 요소들*/
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);
        tempmarker.setIcon(bitmap);
        tempmarker.setPosition(0.5f, 1.0f);

        configureMapView();

        initView();

        mCoursePointX = new ArrayList<>();
        mCoursePointY = new ArrayList<>();
        m_CoursePointCounter = 0;

        permissionManager.request(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new PermissionManager.PermissionListener() {
            @Override
            public void granted() {
                gps = new TMapGpsManager(MapActivity2.this);
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

        if (!m_PathDraw) {
            drawMapPath();
            m_PathDraw = !m_PathDraw;
        }
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
                LogManager.printLog("MapActivity onEnableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());

            }
        });

        mMapView.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {

            @Override
            public void onDisableScrollWithZoomLevelEvent(float zoom, TMapPoint centerPoint) {
                LogManager.printLog("MapActivity onDisableScrollWithZoomLevelEvent " + zoom + " " + centerPoint.getLatitude() + " " + centerPoint.getLongitude());
            }
        });

        mMapView.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {


                LogManager.printLog("MapActivity onPressUpEvent " + markerlist.size());
                return false;
            }

            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> markerlist, ArrayList<TMapPOIItem> poilist, TMapPoint point, PointF pointf) {
                LogManager.printLog("MapActivity onPressEvent " + markerlist.size());

                for (int i = 0; i < markerlist.size(); i++) {
                    TMapMarkerItem item = markerlist.get(i);
                    LogManager.printLog("MapActivity onPressEvent " + item.getName() + " " + item.getTMapPoint().getLatitude() + " " + item.getTMapPoint().getLongitude());
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
                Common.showAlertDialog(MapActivity2.this, "Callout Right Button", strMessage);
            }
        });

        mMapView.setOnClickReverseLabelListener(new TMapView.OnClickReverseLabelListenerCallback() {
            @Override
            public void onClickReverseLabelEvent(TMapLabelInfo findReverseLabel) {

            }
        });
        m_bShowMapIcon = true;
        m_bSightVisible = true;
        m_bTrackingMode = true;
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
            case R.id.btnGPS:
                setCenter();
                break;
        }
    }

    /**
     * setMapIcon
     * 현재위치로 표시될 아이콘을 설정한다.
     */
    public void setMapIcon() {
        if (!m_bShowMapIcon) {
            m_bShowMapIcon = !m_bShowMapIcon;
        }

        if (m_bShowMapIcon) {
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.gpsloaction);
            mMapView.setIcon(bitmap);
        }
        mMapView.setIconVisibility(m_bShowMapIcon);
    }

    /**
     * setCompassMode
     * 단말의 방항에 따라 움직이는 나침반모드로 설정한다.
     */
    public void setCompassMode() {
        if (!mMapView.getIsCompass()) {
            mMapView.setCompassMode(!mMapView.getIsCompass());
        }
    }

    /**
     * getIsCompass
     * 나침반모드의 사용여부를 반환한다.
     */
    public void getIsCompass() {
        Boolean bGetIsCompass = mMapView.getIsCompass();
        Common.showAlertDialog(this, "", "현재 나침반 모드는 : " + bGetIsCompass.toString());
    }

    /**
     * setSightVisible
     * 시야표출여부를 설정한다.
     */
    public void setSightVisible() {
        if (!m_bSightVisible) {
            m_bSightVisible = !m_bSightVisible;
        }
        mMapView.setSightVisible(m_bSightVisible);
    }

    public void setCenter() {
        if (!m_bTrackingMode) {
            m_bTrackingMode = !m_bTrackingMode;
            mMapView.setTrackingMode(m_bTrackingMode);
        } else if (m_bTrackingMode) {
            m_bTrackingMode = !m_bTrackingMode;
            mMapView.setTrackingMode(m_bTrackingMode);
            m_bTrackingMode = !m_bTrackingMode;
            mMapView.setTrackingMode(m_bTrackingMode);
        }
    }

    /**
     * setTrackingMode
     * 화면중심을 단말의 현재위치로 이동시켜주는 트래킹모드로 설정한다.
     */
    public void setTrackingMode() {
        if (!m_bTrackingMode) {
            m_bTrackingMode = !m_bTrackingMode;
        }
        mMapView.setTrackingMode(m_bTrackingMode);
    }

    /**
     * drawMapPath
     * 지도에 시작-종료 점에 대해서 경로를 표시한다.
     */
    public void drawMapPath() {
        mCoursePointX = (ArrayList<Double>) mCourse.getLatitude();
        mCoursePointY = (ArrayList<Double>) mCourse.getLongitude();

        TMapData tmapdata = new TMapData();

        TMapMarkerItem pointmarker1 = new TMapMarkerItem();
        TMapMarkerItem pointmarker2 = new TMapMarkerItem();

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.poi_dot);

        pointmarker1.setIcon(bitmap);
        pointmarker1.setPosition(0.5f, 1.0f);

        pointmarker2.setIcon(bitmap);
        pointmarker2.setPosition(0.5f, 1.0f);

        TMapPoint pastpoint = new TMapPoint(0, 0);
        for (int i = 0; i < mCoursePointX.size(); i++) {
            if (m_CoursePointCounter == 0) {

                ClickPoint.setLatitude(mCoursePointX.get(m_CoursePointCounter));
                ClickPoint.setLongitude(mCoursePointY.get(m_CoursePointCounter));
                //마커 설정
                pointmarker1.setTMapPoint(ClickPoint);
                pointmarker1.setName("startmarker" + 0);
                mMapView.addMarkerItem("pointmarkerzxcv" + 0, pointmarker1);

                pastpoint.setLatitude(mMapView.getLatitude());
                pastpoint.setLongitude(mMapView.getLongitude());

                //Log.i("시작m_CoursePoint", Integer.toString(m_CoursePointCounter));
                m_CoursePointCounter++;
            } else if (m_CoursePointCounter > 0) {

                ClickPoint.setLatitude(mCoursePointX.get(m_CoursePointCounter));
                ClickPoint.setLongitude(mCoursePointY.get(m_CoursePointCounter));

                if (m_CoursePointCounter == (mCoursePointX.size() - 1)) {
                    pointmarker2.setTMapPoint(ClickPoint);
                    pointmarker2.setName("endmarker" + 2);
                    mMapView.addMarkerItem("pointmark" + 2, pointmarker2);
                }
                pastpoint.setLatitude(mCoursePointX.get(m_CoursePointCounter - 1));
                pastpoint.setLongitude(mCoursePointY.get(m_CoursePointCounter - 1));

                tmapdata.findPathDataWithType(TMapPathType.CAR_PATH, pastpoint, ClickPoint, new FindPathDataListenerCallback() {

                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        mMapView.addTMapPolyLine("Line" + (m_CoursePointCounter), polyLine);
                    }
                });
                //.i("m_CoursePoint", Integer.toString(m_CoursePointCounter));
                m_CoursePointCounter++;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}

