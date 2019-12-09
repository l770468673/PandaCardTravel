package com.pandacard.teavel.uis;import android.app.PendingIntent;import android.content.Intent;import android.content.IntentFilter;import android.nfc.NfcAdapter;import android.nfc.tech.IsoDep;import android.nfc.tech.NfcF;import android.nfc.tech.NfcV;import android.os.Bundle;import android.text.TextUtils;import android.view.View;import android.widget.Button;import android.widget.LinearLayout;import android.widget.RadioButton;import android.widget.RelativeLayout;import android.widget.TextView;import android.widget.Toast;import androidx.appcompat.app.AppCompatActivity;import com.crb.cttic.pay.card.bean.CardInfoGather;import com.crb.cttic.pay.device.SmartCardNfcTag;import com.crb.cttic.pay.model.account.pay.CrbPayData;import com.crb.cttic.pay.model.account.pay.ExcepOrder;import com.crb.cttic.pay.model.account.pay.ProductModel;import com.crb.cttic.pay.model.account.pay.RechargeOnLineOrderData;import com.crb.cttic.pay.model.account.pay.RefundApplyData;import com.crb.cttic.pay.mvp.view.activity.model.entity.CardManageData;import com.crb.cttic.pay.pay.PayActivity;import com.crb.cttic.pay.utils.CircleDepositCardUtils;import com.crb.cttic.pay.utils.CtticAppSwithAppUtils;import com.crb.cttic.pay.utils.PlaceOrderUtils;import com.crb.cttic.pay.utils.ReadCardUtils;import com.pandacard.teavel.ParamConst;import com.pandacard.teavel.R;import com.pandacard.teavel.utils.LUtils;public class NFCActivity extends AppCompatActivity implements View.OnClickListener {    private static String TAG = "NFCActivity";    public static String[][] TECHLISTS;    public static IntentFilter[] FILTERS;    //订单号    private String orderId;    private String cardId;    private final String ALIPAY = "02";    static {        TECHLISTS = new String[][]{                {IsoDep.class.getName()},                {NfcV.class.getName()}, {NfcF.class.getName()},};        try {            FILTERS = new IntentFilter[]{                    new IntentFilter(                            NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};        } catch (IntentFilter.MalformedMimeTypeException e) {            e.printStackTrace();        }    }    private PendingIntent pendingIntent;    private NfcAdapter nfcAdapter;    private int state;    private CardManageData Mdata = null;    private boolean depositStatus;    private RadioButton mRadio_btn_recharge;    private RelativeLayout mRelay_recharge;    private LinearLayout mLly_showlayout;    private TextView mLly_writecardnum;    private LinearLayout mLly_writeview;    private Button mQuancun;    private Button mQuancun2;    private Button tuikuan;    private String mAppletNo;    private long mCardBalance;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_nfc);        //        Mdata = (CardManageData) getIntent().getSerializableExtra("CardManageData");        if (Mdata != null) {            //圈存            state = 1;            LUtils.i(TAG, "data != null");        } else {            //读卡            state = 0;            LUtils.i(TAG, "data == null");        }        //初始化NFC        initNfc();        initView();    }    private void initView() {        mRadio_btn_recharge = findViewById(R.id.radio_btn_recharge);        mRelay_recharge = findViewById(R.id.relay_recharge);        mLly_showlayout = findViewById(R.id.lly_showlayout);        mLly_writeview = findViewById(R.id.lly_writeview);        mLly_writecardnum = findViewById(R.id.lly_writecardnum);        mQuancun = findViewById(R.id.quancun);        mQuancun2 = findViewById(R.id.quancun2);        tuikuan = findViewById(R.id.tuikuan);        mRadio_btn_recharge.setOnClickListener(this);        mQuancun.setOnClickListener(this);        mQuancun2.setOnClickListener(this);        tuikuan.setOnClickListener(this);    }    @Override    protected void onNewIntent(Intent intent) {        super.onNewIntent(intent);        SmartCardNfcTag ctticReader = SmartCardNfcTag.getInstance(intent);        switch (state) {            case 0:                //读卡                ReadCardUtils.getInstance().getReadCardInfo(NFCActivity.this, "", ctticReader, new ReadCardUtils.ReadCardUtilsListener() {                    @Override                    public void readCardFail(int errCode, String errMes) {                        showShortToast("读卡失败 errCode =" + errCode + "errMes=" + errMes.toString());                        LUtils.i(TAG, errMes.toString());                    }                    @Override                    public void readCardSuccess(CardInfoGather cardInfoGather) {                        //                        showShortToast("读卡成功" + cardInfoGather.toString());                        setResult(RESULT_OK, new Intent().putExtra(ParamConst.CARD_INFO_GATHER, cardInfoGather));                        LUtils.i(TAG, cardInfoGather.toString());                        mLly_writeview.setVisibility(View.VISIBLE);                        mRelay_recharge.setVisibility(View.GONE);                        mAppletNo = cardInfoGather.getPublicBasicInfo().getAppletNo();                        mCardBalance = cardInfoGather.getCardBalance();                        mLly_writecardnum.setText(mAppletNo);                    }                });                break;            case 1:                //圈存                if (depositStatus) {                    showShortToast("您已经圈存过了");                    return;                }                CircleDepositCardUtils.getInstance().toCircleDeposiCard(NFCActivity.this, ctticReader, Mdata, new CircleDepositCardUtils.CircleDepositCardListener() {                    @Override                    public void cardRechargeSuccess() {                        // LogUtil.e("圈存成功");                        showShortToast("圈存成功");                        LUtils.i(TAG, "data == 圈存成功" + Mdata);                        depositStatus = true;                    }                    @Override                    public void cardRechargeFail() {                        // LogUtil.e("圈存失败");                        showShortToast("圈存失败");                        LUtils.e(TAG, "data == 圈存失败" + Mdata);                    }                });                break;        }    }    public void quancunMoney() {        //圈存        final CardManageData data = new CardManageData();        data.setActionType("0x00");        data.setCardNum(mAppletNo);        if (!TextUtils.isEmpty(cardId)) {            data.setCardNum(cardId);        }        LUtils.e(TAG, "orderId===" + orderId);        if (!TextUtils.isEmpty(orderId)) {            data.setOrderId(orderId);        } else {            showShortToast("请先下单或查询是否有异常订单，再进行圈存");            return;        }        data.setSpId("05212482FF");        data.setPhoneNo("17709690435");        data.setBalance((int) mCardBalance);        data.setAmount(1);        Mdata = data;        state = 1;        LUtils.e(TAG, "圈存 data " + data.toString());        LUtils.e(TAG, "圈存 Mdata " + Mdata.toString());    }    protected void showShortToast(String message) {        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();    }    private void initNfc() {        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);        nfcAdapter = NfcAdapter.getDefaultAdapter(this);    }    @Override    protected void onResume() {        super.onResume();        if (nfcAdapter != null) {            nfcAdapter.enableForegroundDispatch(this, pendingIntent,                    FILTERS, TECHLISTS);        }    }    @Override    protected void onPause() {        super.onPause();        if (nfcAdapter != null) {            nfcAdapter.disableForegroundDispatch(this);        }    }    @Override    protected void onActivityResult(int requestCode, int resultCode, Intent data) {        super.onActivityResult(requestCode, resultCode, data);        if (resultCode == PayActivity.PLACE_ORDER_SUCCESS) {            int payState = data.getIntExtra(PayActivity.PAY_STATE, -1);            if (payState == CtticAppSwithAppUtils.PAY_SUCCESS) {                CrbPayData payData = (CrbPayData) data.getSerializableExtra(PayActivity.PAY_RESULT_KET);                orderId = payData.getOrderId();                //                ShareStoreManager.getInstance(getApplicationContext()).getAppShareStore().putString("orderId", orderId);                showShortToast("支付成功, quancunMoney()");                quancunMoney();            } else {                orderId = null;                showShortToast("支付失败");            }        } else if (resultCode == RESULT_OK && requestCode == ParamConst.READ_CARD_INFO_CODE) {            CardInfoGather cardInfoGather = (CardInfoGather) data.getSerializableExtra(ParamConst.CARD_INFO_GATHER);            if (null != cardInfoGather) {                cardId = cardInfoGather.getPublicBasicInfo().getAppletNo();                LUtils.i(TAG, "card Id is " + cardInfoGather.getPublicBasicInfo().getAppletNo());                showShortToast("卡号：" + cardId);            }        }    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.radio_btn_recharge:                mLly_showlayout.setVisibility(View.GONE);                mRelay_recharge.setVisibility(View.VISIBLE);                break;            case R.id.quancun2:                //                quancunMoney();                break;            case R.id.tuikuan:                PlaceOrderUtils.refundApplication(orderId, "", new PlaceOrderUtils.RefundListenter() {                    @Override                    public void refundApplySuccess(RefundApplyData orderData) {                        showShortToast("退款申请" + orderData.toString());                        orderId = null;                    }                    @Override                    public void refundApplyFail() {                        showShortToast("退款失败");                    }                });                break;            case R.id.quancun:                final ProductModel productModel = new ProductModel();                productModel.setAmout(1);                productModel.setCardNum(mAppletNo);                if (!TextUtils.isEmpty(cardId)) {                    productModel.setCardNum(cardId);                }                productModel.setTradeType(2);                productModel.setPayChannel(ALIPAY);                productModel.setInstanceAid("");                productModel.setSeId("");                productModel.setMerchant(productModel.getMerchant());                PlaceOrderUtils.excepOrder(TextUtils.isEmpty(cardId) ? mAppletNo : cardId, new PlaceOrderUtils.ExcepOrderListener() {                    @Override                    public void abnormalOrders(ExcepOrder order) {                        orderId = order.getOrderId();                        showShortToast("异常订单" + order.toString() + "\n请直接圈存上一次的订单");                    }                    @Override                    public void abnormalOrdersNo() {                        showShortToast("没有异常订单,去支付");                        //商品详情基本信息  -------productModel                        PlaceOrderUtils.toPlaceOrder(NFCActivity.this, productModel, new PlaceOrderUtils.PlaceOrderListener() {                            @Override                            public void placeOrderSuccess(RechargeOnLineOrderData orderData) {                                //下单成功去支付                                RechargeOnLineOrderData onLineOrderData = new RechargeOnLineOrderData(orderData.getOrderId(), orderData.getAmout(), orderData.getOrderTime(), orderData.getResultParams());                                CtticAppSwithAppUtils.startPayment(NFCActivity.this, onLineOrderData);                            }                            @Override                            public void placeOrderFail() {                                showShortToast("下单失败");                            }                        });                    }                });                break;        }    }}