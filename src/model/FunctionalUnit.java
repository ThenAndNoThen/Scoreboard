package model;

public class FunctionalUnit {
	final public static String INTEGER="Integer";
	final public static String MUTL1="Mutl1";
	final public static String MUTL2="Mutl2";
	final public static String ADD="Add";
	final public static String DIVIDE="Divide";
	public static final int UNIT_NUM=5;
	public static int UNIT_COUNT=0;
	
	private int mUnitNum;
	private boolean mIsUnitBusy;
	private String mUnitName;
	private InstructionBean mInstructionBean;
	private String mUnitForJ;
	private String mUnitForK;
	private boolean mIsJReady;
	private boolean mIsKReady;
	private int mTimeCost;
	private int mTimeLeft;
	
	public FunctionalUnit(String unitname){
		this.mUnitNum=UNIT_COUNT;
		UNIT_COUNT++;
		this.mIsUnitBusy=false;
		this.mUnitName=unitname;
		this.mInstructionBean=null;
		this.mUnitForJ=null;
		this.mUnitForK=null;
		this.mIsJReady=false;
		this.mIsKReady=false;
		switch(mUnitName){
		case INTEGER:
			this.mTimeCost=1;
			break;
		case MUTL1:
			this.mTimeCost=10;
			break;
		case MUTL2:
			this.mTimeCost=10;
			break;
		case ADD:
			this.mTimeCost=2;
			break;
		case DIVIDE:
			this.mTimeCost=40;
			break;
		}
	}
	public int getmTimeLeft() {
		return mTimeLeft;
	}
	
	public int getmTimeCost() {
		return mTimeCost;
	}
	
	public InstructionBean getmInstructionBean() {
		return mInstructionBean;
	}
	
	public String getmUnitForJ() {
		return mUnitForJ;
	}
	
	public String getmUnitForK() {
		return mUnitForK;
	}
	
	public String getmUnitName() {
		return mUnitName;
	}
	
	public boolean getmIsUnitBusy(){
		return mIsUnitBusy;
	}
	
	public boolean getmIsJReady(){
		return mIsJReady;
	}
	
	public boolean getmIsKReady(){
		return mIsKReady;
	}

    public int getmUnitNum() {
        return mUnitNum;
    }

        
	
        
	
	
	
	
	
	
	
	public void setmInstructionBean(InstructionBean mInstructionBean) {
		this.mInstructionBean = mInstructionBean;
	}
	
	public void setmIsUnitBusy(boolean mIsUnitBusy) {
		this.mIsUnitBusy = mIsUnitBusy;
	}
	
	public void setmIsJReady(boolean mIsJReady) {
		this.mIsJReady = mIsJReady;
	}
	
	public void setmIsKReady(boolean mIsKReady) {
		this.mIsKReady = mIsKReady;
	}
	
	public void setmUnitForJ(String mUnitForJ) {
		this.mUnitForJ = mUnitForJ;
	}
	
	public void setmUnitForK(String mUnitForK) {
		this.mUnitForK = mUnitForK;
	}
	
	public void setmUnitName(String mUnitName) {
		this.mUnitName = mUnitName;
	}
	
	public void setmTimeLeft(int mTimeLeft) {
		this.mTimeLeft = mTimeLeft;
	}
	
}
